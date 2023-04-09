package io.hamal.application.adapter

import io.hamal.lib.domain_notification.*
import io.hamal.lib.domain_notification.DomainNotificationHandler.Container
import io.hamal.lib.meta.exception.InternalServerException
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import java.time.Duration
import java.util.concurrent.LinkedBlockingQueue
import kotlin.reflect.KClass

val queueStore = mutableMapOf<String, LinkedBlockingQueue<DomainNotification>>()


class DomainNotificationAdapter(
    val scheduledExecutorService: ThreadPoolTaskScheduler
) : NotifyDomainPort, CreateDomainNotificationConsumerPort {

    override fun <NOTIFICATION : DomainNotification> invoke(notification: NOTIFICATION) {
        queueStore.putIfAbsent(notification.topic, LinkedBlockingQueue())
        queueStore[notification.topic]!!.add(notification)

    }

    override fun create(): DomainNotificationConsumer {
        return object : DomainNotificationConsumer {

            val handlerContainer = Container.DefaultImpl()

            override fun <NOTIFICATION : DomainNotification> register(
                clazz: KClass<NOTIFICATION>,
                receiver: DomainNotificationHandler<NOTIFICATION>
            ): DomainNotificationConsumer {
                if (handlerContainer.register(clazz, receiver)) {
                    val topic = clazz.topic()
                    scheduledExecutorService.scheduleAtFixedRate(
                        {
                            queueStore[topic]?.take()
                                ?.let { notification ->
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