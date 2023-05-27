package io.hamal.backend.event.component

import io.hamal.backend.event.Event
import io.hamal.backend.event.topic
import io.hamal.backend.event_handler.EventHandler
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.reflect.KClass

class EventHandlerContainer : EventHandler.Container {
    private val receiverMapping = mutableMapOf<
            KClass<out Event>,
            List<EventHandler<Event>>
            >()

    private val lock = ReentrantReadWriteLock()

    override fun <EVENT : Event> register(
        clazz: KClass<EVENT>,
        receiver: EventHandler<EVENT>
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
    override fun <EVENT : Event> get(clazz: KClass<EVENT>): List<EventHandler<EVENT>> {
        try {
            lock.readLock().lock()
            return receiverMapping[clazz]
                ?.map { it as EventHandler<EVENT> }
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
