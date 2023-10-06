package io.hamal.core.event

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.repository.api.event.PlatformEvent
import io.hamal.repository.api.event.topicName
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.reflect.KClass

interface PlatformEventHandler<out EVENT : PlatformEvent> {

    fun handle(cmdId: CmdId, evt: @UnsafeVariance EVENT)

}

class PlatformEventContainer {
    private val receiverMapping = mutableMapOf<
            KClass<out PlatformEvent>,
            List<PlatformEventHandler<PlatformEvent>>
            >()

    private val lock = ReentrantReadWriteLock()

    fun <EVENT : PlatformEvent> register(
        clazz: KClass<EVENT>,
        receiver: PlatformEventHandler<EVENT>
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
    operator fun <EVENT : PlatformEvent> get(clazz: KClass<EVENT>): List<PlatformEventHandler<EVENT>> {
        try {
            lock.readLock().lock()
            return receiverMapping[clazz]
                ?.map { it as PlatformEventHandler<EVENT> }
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
