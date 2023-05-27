package io.hamal.backend.event.service

import io.hamal.backend.event.Event
import io.hamal.backend.event.component.EventHandlerContainer
import io.hamal.backend.event_handler.EventHandler
import io.hamal.backend.repository.api.log.BrokerRepository
import io.hamal.backend.repository.api.log.GroupId
import io.hamal.backend.repository.sqlite.log.ProtobufConsumer
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

    fun <NOTIFICATION : Event> register(
        clazz: KClass<NOTIFICATION>,
        handler: EventHandler<NOTIFICATION>
    ): EventProcessorFactory

    fun create(): EventProcessor
}

class DefaultEventProcessor(
    val scheduledExecutorService: ThreadPoolTaskScheduler,
    val brokerRepository: BrokerRepository
) : EventProcessorFactory {

    private val handlerContainer = EventHandlerContainer()

    override fun <NOTIFICATION : Event> register(
        clazz: KClass<NOTIFICATION>,
        handler: EventHandler<NOTIFICATION>
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
                    .map(brokerRepository::resolveTopic)

                allDomainTopics.forEach { topic ->
                    val consumer = ProtobufConsumer(
                        GroupId("domain-notification-processor"),
                        topic,
                        brokerRepository,
                        Event::class
                    )

                    scheduledTasks.add(
                        scheduledExecutorService.scheduleAtFixedRate(
                            {
                                consumer.consume(100) { notification ->
                                    CompletableFuture.runAsync {
                                        handlerContainer[notification::class].forEach { handler ->
                                            try {
                                                handler.handle(notification)
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