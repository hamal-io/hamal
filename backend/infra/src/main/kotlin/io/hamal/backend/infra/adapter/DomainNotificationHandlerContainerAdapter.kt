package io.hamal.backend.infra.adapter

import io.hamal.backend.core.notification.DomainNotification
import io.hamal.backend.core.notification.topic
import io.hamal.backend.core.port.notification.HandleDomainNotificationPort
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.reflect.KClass

class DomainNotificationHandlerContainerAdapter : HandleDomainNotificationPort.Container {
    private val receiverMapping = mutableMapOf<
            KClass<out DomainNotification>,
            List<HandleDomainNotificationPort<DomainNotification>>
            >()

    private val lock = ReentrantReadWriteLock()

    override fun <NOTIFICATION : DomainNotification> register(
        clazz: KClass<NOTIFICATION>,
        receiver: HandleDomainNotificationPort<NOTIFICATION>
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
    override fun <NOTIFICATION : DomainNotification> get(clazz: KClass<NOTIFICATION>): List<HandleDomainNotificationPort<NOTIFICATION>> {
        try {
            lock.readLock().lock()
            return receiverMapping[clazz]
                ?.map { it as HandleDomainNotificationPort<NOTIFICATION> }
                ?: listOf()
        } finally {
            lock.readLock().unlock()
        }
    }

    override fun topics(): Set<String> {
        try {
            lock.readLock().lock()
            return receiverMapping.keys
                .map { it.topic() }
                .toSet()
        } finally {
            lock.readLock().unlock()
        }
    }
}
