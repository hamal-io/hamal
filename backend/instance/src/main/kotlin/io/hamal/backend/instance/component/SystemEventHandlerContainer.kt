package io.hamal.backend.instance.component

import io.hamal.backend.instance.event.SystemEvent
import io.hamal.backend.instance.event.handler.SystemEventHandler
import io.hamal.backend.instance.event.topic
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.reflect.KClass

class SystemEventHandlerContainer : SystemEventHandler.Container {
    private val receiverMapping = mutableMapOf<
            KClass<out SystemEvent>,
            List<SystemEventHandler<SystemEvent>>
            >()

    private val lock = ReentrantReadWriteLock()

    override fun <EVENT : SystemEvent> register(
        clazz: KClass<EVENT>,
        receiver: SystemEventHandler<EVENT>
    ): Boolean {
        try {
            lock.writeLock().lock()
            val result = !receiverMapping.containsKey(clazz)
            receiverMapping.putIfAbsent(clazz, mutableListOf())
            receiverMapping[clazz] = receiverMapping[clazz]!!.plus(receiver)
            return result
        } finally {
            lock.writeLock().unlock()
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <EVENT : SystemEvent> get(clazz: KClass<EVENT>): List<SystemEventHandler<EVENT>> {
        try {
            lock.readLock().lock()
            return receiverMapping[clazz]
                ?.map { it as SystemEventHandler<EVENT> }
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
