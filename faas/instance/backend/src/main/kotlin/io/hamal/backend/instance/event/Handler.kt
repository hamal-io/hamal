package io.hamal.backend.instance.event

import io.hamal.backend.instance.event.event.InstanceEvent
import io.hamal.backend.instance.event.event.topicName
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.TopicName
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.reflect.KClass

interface InstanceEventHandler<out EVENT : InstanceEvent> {

    fun handle(cmdId: CmdId, evt: @UnsafeVariance EVENT)

}

class InstanceEventContainer {
    private val receiverMapping = mutableMapOf<
            KClass<out InstanceEvent>,
            List<InstanceEventHandler<InstanceEvent>>
            >()

    private val lock = ReentrantReadWriteLock()

    fun <EVENT : InstanceEvent> register(
        clazz: KClass<EVENT>,
        receiver: InstanceEventHandler<EVENT>
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
    operator fun <EVENT : InstanceEvent> get(clazz: KClass<EVENT>): List<InstanceEventHandler<EVENT>> {
        try {
            lock.readLock().lock()
            return receiverMapping[clazz]
                ?.map { it as InstanceEventHandler<EVENT> }
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
