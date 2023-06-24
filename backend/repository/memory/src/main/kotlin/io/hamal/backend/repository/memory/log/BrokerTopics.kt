package io.hamal.backend.repository.memory.log

import io.hamal.backend.repository.api.log.LogBrokerTopicsRepository
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock


class MemoryLogBrokerTopicsRepository : LogBrokerTopicsRepository<MemoryLogTopic> {

    private val lock = ReentrantLock()
    private val topicMapping = mutableMapOf<TopicName, MemoryLogTopic>()
    override fun create(cmdId: CmdId, toCreate: LogBrokerTopicsRepository.TopicToCreate): MemoryLogTopic {
        return lock.withLock {
            require(!topicMapping.containsKey(toCreate.name)) { "${toCreate.name} already exists" }
            topicMapping[toCreate.name] = MemoryLogTopic(
                id = toCreate.id,
                name = toCreate.name
            )
            topicMapping[toCreate.name]!!
        }
    }

    override fun find(name: TopicName): MemoryLogTopic? {
        return lock.withLock {
            topicMapping[name]
        }
    }

    override fun find(id: TopicId): MemoryLogTopic? = lock.withLock {
        topicMapping.values.find { it.id == id }
    }

    override fun query(): List<MemoryLogTopic> = lock.withLock {
        topicMapping.values.toList()
    }

    override fun count(): ULong {
        return lock.withLock { topicMapping.size.toULong() }
    }

    fun clear() {
        topicMapping.clear()
    }

    override fun close() {
    }
}