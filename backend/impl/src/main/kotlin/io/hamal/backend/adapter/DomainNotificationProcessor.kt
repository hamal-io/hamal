package io.hamal.backend.adapter

import io.hamal.backend.core.notification.port.HandleDomainNotificationPort
import io.hamal.backend.notification.DomainNotification
import io.hamal.backend.repository.api.log.BrokerRepository
import io.hamal.backend.repository.api.log.Consumer
import io.hamal.backend.repository.api.log.Topic
import io.hamal.backend.repository.sqlite.log.ProtobufConsumer
import org.springframework.beans.factory.DisposableBean
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ScheduledFuture
import kotlin.reflect.KClass

interface DomainNotificationProcessor {
    fun cancel()
}

interface CreateDomainNotificationProcessorPort {

    fun <NOTIFICATION : DomainNotification> register(
        clazz: KClass<NOTIFICATION>,
        handler: HandleDomainNotificationPort<NOTIFICATION>
    ): CreateDomainNotificationProcessorPort

    fun create(): DomainNotificationProcessor
}

class DefaultDomainNotificationProcessor(
    val scheduledExecutorService: ThreadPoolTaskScheduler,
    val brokerRepository: BrokerRepository
) : CreateDomainNotificationProcessorPort {

    private val handlerContainer = DomainNotificationHandlerContainerAdapter()

    override fun <NOTIFICATION : DomainNotification> register(
        clazz: KClass<NOTIFICATION>,
        handler: HandleDomainNotificationPort<NOTIFICATION>
    ): CreateDomainNotificationProcessorPort {
        handlerContainer.register(clazz, handler)
        return this
    }

    override fun create(): DomainNotificationProcessor {
        return object : DomainNotificationProcessor, DisposableBean {

            private val scheduledTasks = mutableListOf<ScheduledFuture<*>>()

            init {
                val allDomainTopics = handlerContainer.topics()
                    .map { Topic.Name(it) }
                    .map(brokerRepository::resolveTopic)

                allDomainTopics.forEach { topic ->
                    val consumer = ProtobufConsumer(
                        Consumer.GroupId("domain-notification-processor"),
                        topic,
                        brokerRepository,
                        DomainNotification::class
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
                            }, Duration.ofMillis(1)
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