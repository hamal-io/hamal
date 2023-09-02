package io.hamal.backend.event

import io.hamal.backend.event.event.HubEvent
import io.hamal.backend.event.event.topicName
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.TopicName
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.reflect.KClass

interface HubEventHandler<out EVENT : HubEvent> {

    fun handle(cmdId: CmdId, evt: @UnsafeVariance EVENT)

}

class HubEventContainer {
    private val receiverMapping = mutableMapOf<
            KClass<out HubEvent>,
            List<HubEventHandler<HubEvent>>
            >()

    private val lock = ReentrantReadWriteLock()

    fun <EVENT : HubEvent> register(
        clazz: KClass<EVENT>,
        receiver: HubEventHandler<EVENT>
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
    operator fun <EVENT : HubEvent> get(clazz: KClass<EVENT>): List<HubEventHandler<EVENT>> {
        try {
            lock.readLock().lock()
            return receiverMapping[clazz]
                ?.map { it as HubEventHandler<EVENT> }
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
