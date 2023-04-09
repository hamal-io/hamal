package io.hamal.application.adapter

import io.hamal.lib.domain_notification.*
import io.hamal.lib.domain_notification.DomainNotificationHandler.Container
import io.hamal.lib.meta.exception.InternalServerException
import io.hamal.module.bus.api.common.Record
import io.hamal.module.bus.api.common.TopicId
import io.hamal.module.bus.impl.consumer.InMemoryConsumer
import io.hamal.module.bus.impl.producer.InMemoryProducer
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import java.time.Duration
import java.util.concurrent.LinkedBlockingQueue
import kotlin.reflect.KClass

val queueStore = mutableMapOf<String, LinkedBlockingQueue<DomainNotification>>()


class DomainNotificationAdapter(
    val scheduledExecutorService: ThreadPoolTaskScheduler
) : NotifyDomainPort, CreateDomainNotificationConsumerPort {

    private val producer = InMemoryProducer<String, DomainNotification>()


    override fun <NOTIFICATION : DomainNotification> invoke(notification: NOTIFICATION) {
//        queueStore.putIfAbsent(notification.topic, LinkedBlockingQueue())
//        queueStore[notification.topic]!!.add(notification)
        producer(
            listOf(
                Record(
                    "SomeKey", notification, TopicId(1)
                )
            )
        )
    }

    override fun create(): DomainNotificationConsumer {
        return object : DomainNotificationConsumer {

            val handlerContainer = Container.DefaultImpl()

            private val consumer = InMemoryConsumer<String, DomainNotification>(
                listOf(
                    TopicId(1)
                )
            )

            override fun <NOTIFICATION : DomainNotification> register(
                clazz: KClass<NOTIFICATION>,
                receiver: DomainNotificationHandler<NOTIFICATION>
            ): DomainNotificationConsumer {
                if (handlerContainer.register(clazz, receiver)) {
                    val topic = clazz.topic()
                    scheduledExecutorService.scheduleAtFixedRate(
                        {
                            consumer().forEach { record ->
                                val notification = record.value
                                handlerContainer[notification::class].forEach { receiver ->
                                    try {
                                        receiver.on(notification)
                                    } catch (t: Throwable) {
                                        throw InternalServerException(t)
                                    }
                                }
                            }
                        }, Duration.ofMillis(1)
                    )
                }
                return this
            }
        }
    }
}