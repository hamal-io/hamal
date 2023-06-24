package io.hamal.backend.service

import io.hamal.backend.component.Async
import io.hamal.backend.component.SystemEventHandlerContainer
import io.hamal.backend.event.SystemEvent
import io.hamal.backend.event.handler.SystemEventHandler
import io.hamal.backend.repository.api.log.*
import io.hamal.lib.common.util.HashUtils.md5
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.domain.vo.port.GenerateDomainId
import org.springframework.beans.factory.DisposableBean
import java.util.concurrent.ScheduledFuture
import kotlin.reflect.KClass
import kotlin.time.Duration.Companion.milliseconds

interface EventService {
    fun cancel()
}

interface SystemEventServiceFactory {

    fun <EVENT : SystemEvent> register(
        clazz: KClass<EVENT>,
        handler: SystemEventHandler<EVENT>
    ): SystemEventServiceFactory

    fun create(): EventService
}

class DefaultSystemEventService<TOPIC : LogTopic>(
    val async: Async,
    val generateDomainId: GenerateDomainId,
    val logBrokerRepository: LogBrokerRepository<TOPIC>
) : SystemEventServiceFactory {

    private val handlerContainer = SystemEventHandlerContainer()

    override fun <EVENT : SystemEvent> register(
        clazz: KClass<EVENT>,
        handler: SystemEventHandler<EVENT>
    ): SystemEventServiceFactory {
        handlerContainer.register(clazz, handler)
        return this
    }

    override fun create(): EventService {
        return object : EventService, DisposableBean {

            private val scheduledTasks = mutableListOf<ScheduledFuture<*>>()

            init {
                val systemEventTopics = handlerContainer.topics()
                    .map { TopicName(it) }
                    .map { topicName ->
                        val topicId = generateDomainId(::TopicId)
                        logBrokerRepository.find(topicName) ?: logBrokerRepository.create(
                            CmdId(topicId),
                            CreateTopic.TopicToCreate(topicId, topicName)
                        )
                    }

                systemEventTopics.forEach { topic ->
                    val consumer = ProtobufLogConsumer(
                        GroupId("event-service"),
                        topic,
                        logBrokerRepository,
                        SystemEvent::class
                    )

                    scheduledTasks.add(
                        async.atFixedRate(1.milliseconds) {
                            consumer.consume(10) { chunkId, evt ->
                                handlerContainer[evt::class].forEach { handler ->
                                    try {
                                        val cmdId = CmdId(md5("${evt.topic}-${chunkId.value.value}"))
                                        handler.handle(cmdId, evt)
                                    } catch (t: Throwable) {
                                        throw Error(t)
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