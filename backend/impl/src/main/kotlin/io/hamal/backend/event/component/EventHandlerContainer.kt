package io.hamal.backend.event.component

import io.hamal.backend.event.Event
import io.hamal.backend.event.handler.EventHandler
import io.hamal.backend.event.topic
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.reflect.KClass

class EventHandlerContainer : EventHandler.Container {
    private val receiverMapping = mutableMapOf<
            KClass<out Event>,
            List<EventHandler<Event>>
            >()

    private val lock = ReentrantReadWriteLock()

    override fun <NOTIFICATION : Event> register(
        clazz: KClass<NOTIFICATION>,
        receiver: EventHandler<NOTIFICATION>
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
    override fun <NOTIFICATION : Event> get(clazz: KClass<NOTIFICATION>): List<EventHandler<NOTIFICATION>> {
        try {
            lock.readLock().lock()
            return receiverMapping[clazz]
                ?.map { it as EventHandler<NOTIFICATION> }
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
