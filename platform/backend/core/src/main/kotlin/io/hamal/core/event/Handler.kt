package io.hamal.core.event

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.repository.api.event.InternalEvent
import io.hamal.repository.api.event.topicName
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.reflect.KClass

interface InternalEventHandler<out EVENT : InternalEvent> {

    fun handle(cmdId: CmdId, evt: @UnsafeVariance EVENT)

}

class InternalEventContainer {

    private val receiverMapping = mutableMapOf<
            KClass<out InternalEvent>,
            List<InternalEventHandler<InternalEvent>>
            >()

    private val lock = ReentrantReadWriteLock()

    fun <EVENT : InternalEvent> register(
        clazz: KClass<EVENT>,
        receiver: InternalEventHandler<EVENT>
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
    operator fun <EVENT : InternalEvent> get(clazz: KClass<EVENT>): List<InternalEventHandler<EVENT>> {
        try {
            lock.readLock().lock()
            return receiverMapping[clazz]
                ?.map { it as InternalEventHandler<EVENT> }
                ?: listOf()
        } finally {
            lock.readLock().unlock()
        }
    }

    fun topicNames(): Set<TopicName> {
        try {
            lock.readLock().lock()
            return receiverMapping.keys
                .map { it.topicName() }
                .toSet()
        } finally {
            lock.readLock().unlock()
        }
    }
}
