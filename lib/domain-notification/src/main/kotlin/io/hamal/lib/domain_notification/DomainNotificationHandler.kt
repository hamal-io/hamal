package io.hamal.lib.domain_notification

import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.reflect.KClass

interface DomainNotificationHandler<out NOTIFICATION : DomainNotification> {

    fun on(notification: @UnsafeVariance NOTIFICATION)

    interface Container {

        fun <NOTIFICATION : DomainNotification> register(
            clazz: KClass<NOTIFICATION>,
            receiver: DomainNotificationHandler<NOTIFICATION>
        ): Boolean

        operator fun <NOTIFICATION : DomainNotification> get(clazz: KClass<NOTIFICATION>): List<DomainNotificationHandler<NOTIFICATION>>

        open class DefaultImpl : Container {
            private val receiverMapping = mutableMapOf<
                    KClass<out DomainNotification>,
                    List<DomainNotificationHandler<DomainNotification>>
                    >()

            private val lock = ReentrantReadWriteLock()

            override fun <NOTIFICATION : DomainNotification> register(
                clazz: KClass<NOTIFICATION>,
                receiver: DomainNotificationHandler<NOTIFICATION>
            ): Boolean {
                try {
                    lock.writeLock().lock()
                    //FIXME ensure notification is annotated or refuse
                    val result = !receiverMapping.containsKey(clazz)
                    receiverMapping.putIfAbsent(clazz, mutableListOf())
                    receiverMapping[clazz] = receiverMapping[clazz]!!.plus(receiver)
                    return result
                } finally {
                    lock.writeLock().unlock()
                }
            }

            @Suppress("UNCHECKED_CAST")
            override fun <NOTIFICATION : DomainNotification> get(clazz: KClass<NOTIFICATION>): List<DomainNotificationHandler<NOTIFICATION>> {
                try {
                    lock.readLock().lock()
                    return receiverMapping[clazz]
                        ?.map { it as DomainNotificationHandler<NOTIFICATION> }
                        ?: listOf()
                } finally {
                    lock.readLock().unlock()
                }
            }
        }
    }
}


