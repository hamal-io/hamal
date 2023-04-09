package io.hamal.application.adapter

import io.hamal.lib.domain_notification.*
import io.hamal.lib.meta.exception.InternalServerException
import org.springframework.beans.factory.DisposableBean
import org.springframework.stereotype.Component
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

val queueStore = mutableMapOf<String, LinkedBlockingQueue<DomainNotification>>()

@Component
class DomainNotificationAdapter : HandleDomainNotificationPort,
    NotifyDomainPort, DisposableBean {

    val handlerContainer: DomainNotificationHandler.Container = DomainNotificationHandler.Container.DefaultImpl()

    // FIXME get this via bean there should only be one thread pool
    val executorService = Executors.newScheduledThreadPool(2)

    override fun <NOTIFICATION : DomainNotification> invoke(notification: NOTIFICATION) {
        queueStore.putIfAbsent(notification.topic()!!, LinkedBlockingQueue())
        queueStore[notification.topic()]!!.add(notification)

    }


    override fun destroy() {
        TODO("Not yet implemented")
    }

    override fun <NOTIFICATION : DomainNotification> register(
        clazz: KClass<NOTIFICATION>,
        receiver: DomainNotificationHandler<NOTIFICATION>
    ): HandleDomainNotificationPort {
        if (handlerContainer.register(clazz, receiver)) {
            val topic = clazz.topic()   // FIXME perfect candidate for KeyedOnce!
            executorService.scheduleAtFixedRate(
                {
                    for (i in 0..1000) {
                        val polled = queueStore[topic]?.take() ?: break
                        polled?.let { notification ->
                            handlerContainer[notification::class].forEach { receiver ->
                                try {
                                    receiver.on(notification)
                                } catch (t: Throwable) {
                                    throw InternalServerException(t)
                                }
                            }
                        }
                    }
                },
                1, 1, TimeUnit.MILLISECONDS
            )
        }

        return this
    }

}