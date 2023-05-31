package io.hamal.backend.service

import io.hamal.backend.component.EventHandlerContainer
import io.hamal.backend.event.Event
import io.hamal.backend.event_handler.EventHandler
import io.hamal.backend.repository.api.log.GroupId
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.backend.repository.sqlite.log.ProtobufLogConsumer
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.vo.TopicName
import org.springframework.beans.factory.DisposableBean
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ScheduledFuture
import kotlin.reflect.KClass

interface EventProcessor {
    fun cancel()
}

interface EventProcessorFactory {

    fun <EVENT : Event> register(
        clazz: KClass<EVENT>,
        handler: EventHandler<EVENT>
    ): EventProcessorFactory

    fun create(): EventProcessor
}

class DefaultEventProcessor(
    val scheduledExecutorService: ThreadPoolTaskScheduler,
    val logBrokerRepository: LogBrokerRepository
) : EventProcessorFactory {

    private val handlerContainer = EventHandlerContainer()

    override fun <EVENT : Event> register(
        clazz: KClass<EVENT>,
        handler: EventHandler<EVENT>
    ): EventProcessorFactory {
        handlerContainer.register(clazz, handler)
        return this
    }

    override fun create(): EventProcessor {
        return object : EventProcessor, DisposableBean {

            private val scheduledTasks = mutableListOf<ScheduledFuture<*>>()

            init {
                val allDomainTopics = handlerContainer.topics()
                    .map { TopicName(it) }
                    .map(logBrokerRepository::resolveTopic)

                allDomainTopics.forEach { topic ->
                    val consumer = ProtobufLogConsumer(
                        GroupId("event-processor"),
                        topic,
                        logBrokerRepository,
                        Event::class
                    )

                    scheduledTasks.add(
                        scheduledExecutorService.scheduleAtFixedRate(
                            {
                                consumer.consume(100) { chunkId, evt ->
                                    CompletableFuture.runAsync {
                                        handlerContainer[evt::class].forEach { handler ->
                                            try {
                                                handler.handle(ReqId(chunkId.value), evt)
                                            } catch (t: Throwable) {
                                                throw Error(t)
                                            }
                                        }
                                    }
                                }
                            }, Duration.ofMillis(10)
                        )
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