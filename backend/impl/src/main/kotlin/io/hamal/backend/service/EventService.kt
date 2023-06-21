package io.hamal.backend.service

import io.hamal.backend.component.Async
import io.hamal.backend.component.EventHandlerContainer
import io.hamal.backend.event.Event
import io.hamal.backend.event_handler.EventHandler
import io.hamal.backend.repository.api.log.GroupId
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.backend.repository.api.log.ProtobufLogConsumer
import io.hamal.lib.common.util.HashUtils.md5
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.vo.TopicName
import org.springframework.beans.factory.DisposableBean
import java.util.concurrent.ScheduledFuture
import kotlin.reflect.KClass
import kotlin.time.Duration.Companion.milliseconds

interface EventService {
    fun cancel()
}

interface EventServiceFactory {

    fun <EVENT : Event> register(
        clazz: KClass<EVENT>,
        handler: EventHandler<EVENT>
    ): EventServiceFactory

    fun create(): EventService
}

class DefaultEventService(
    val async: Async,
    val logBrokerRepository: LogBrokerRepository
) : EventServiceFactory {

    private val handlerContainer = EventHandlerContainer()

    override fun <EVENT : Event> register(
        clazz: KClass<EVENT>,
        handler: EventHandler<EVENT>
    ): EventServiceFactory {
        handlerContainer.register(clazz, handler)
        return this
    }

    override fun create(): EventService {
        return object : EventService, DisposableBean {

            private val scheduledTasks = mutableListOf<ScheduledFuture<*>>()

            init {
                val allDomainTopics = handlerContainer.topics()
                    .map { TopicName(it) }
                    .map(logBrokerRepository::resolveTopic)

                allDomainTopics.forEach { topic ->
                    val consumer = ProtobufLogConsumer(
                        GroupId("event-service"),
                        topic,
                        logBrokerRepository,
                        Event::class
                    )

                    scheduledTasks.add(
                        async.atFixedRate(10.milliseconds) {
                            consumer.consume(100) { chunkId, evt ->
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