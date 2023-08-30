package io.hamal.backend.instance.service

import io.hamal.backend.instance.component.Async
import io.hamal.backend.instance.event.InstanceEventContainer
import io.hamal.backend.instance.event.InstanceEventHandler
import io.hamal.backend.instance.event.events.InstanceEvent
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.util.HashUtils.md5
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.repository.api.log.CreateTopic
import io.hamal.repository.api.log.GroupId
import io.hamal.repository.api.log.LogBrokerRepository
import io.hamal.repository.api.log.ProtobufLogConsumer
import org.springframework.beans.factory.DisposableBean
import java.util.concurrent.ScheduledFuture
import kotlin.reflect.KClass
import kotlin.time.Duration.Companion.milliseconds

interface EventService {
    fun cancel()
}

interface SystemEventServiceFactory {

    fun <EVENT : InstanceEvent> register(
        clazz: KClass<EVENT>,
        handler: InstanceEventHandler<EVENT>
    ): SystemEventServiceFactory

    fun create(): EventService
}

class DefaultSystemEventService(
    val async: Async,
    val generateDomainId: GenerateDomainId,
    val systemEventBrokerRepository: LogBrokerRepository
) : SystemEventServiceFactory {

    private val handlerContainer = InstanceEventContainer()

    override fun <EVENT : InstanceEvent> register(
        clazz: KClass<EVENT>,
        handler: InstanceEventHandler<EVENT>
    ): SystemEventServiceFactory {
        handlerContainer.register(clazz, handler)
        return this
    }

    override fun create(): EventService {
        return object : EventService, DisposableBean {

            private val scheduledTasks = mutableListOf<ScheduledFuture<*>>()

            init {
                val systemEventTopics = handlerContainer.topicNames()
                    .map { topicName ->
                        val topicId = generateDomainId(::TopicId)
                        systemEventBrokerRepository.findTopic(topicName) ?: systemEventBrokerRepository.create(
                            CmdId(topicId),
                            CreateTopic.TopicToCreate(topicId, topicName)
                        )
                    }

                systemEventTopics.forEach { topic ->
                    val consumer = ProtobufLogConsumer(
                        GroupId("instance-event-service"),
                        topic,
                        systemEventBrokerRepository,
                        InstanceEvent::class
                    )

                    scheduledTasks.add(
                        async.atFixedRate(1.milliseconds) {
                            consumer.consume(10) { chunkId, evt ->
                                handlerContainer[evt::class].forEach { handler ->
                                    try {
                                        val cmdId = CmdId(md5("${evt.topicName}-${chunkId.value.value}"))
                                        handler.handle(cmdId, evt)
                                    } catch (t: Throwable) {
                                        t.printStackTrace()
//                                        throw Error(t)
                                    }
                                }
                            }
                        },
                    )
                }
            }

            override fun cancel() {
                scheduledTasks.forEach {
                    it.cancel(false)
                }
            }

            override fun destroy() {
                cancel()
            }
        }
    }
}