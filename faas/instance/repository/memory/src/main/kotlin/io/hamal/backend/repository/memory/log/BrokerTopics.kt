package io.hamal.backend.repository.memory.log

import io.hamal.backend.repository.api.log.BrokerTopicsRepository
import io.hamal.backend.repository.api.log.BrokerTopicsRepository.TopicToCreate
import io.hamal.backend.repository.api.log.LogTopic
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock


class MemoryBrokerTopicsRepository : BrokerTopicsRepository {

    private val lock = ReentrantLock()
    private val topicMapping = mutableMapOf<TopicName, LogTopic>()
    override fun create(cmdId: CmdId, toCreate: TopicToCreate): LogTopic {
        return lock.withLock {
            require(topicMapping.values.none { it.id == toCreate.id }) { "Topic already exists" }
            require(!topicMapping.containsKey(toCreate.name)) { "Topic already exists" }
            topicMapping[toCreate.name] = LogTopic(
                id = toCreate.id,
                name = toCreate.name
            )
            topicMapping[toCreate.name]!!
        }
    }

    override fun find(name: TopicName): LogTopic? {
        return lock.withLock {
            topicMapping[name]
        }
    }

    override fun find(id: TopicId): LogTopic? = lock.withLock {
        topicMapping.values.find { it.id == id }
    }

    override fun list(block: BrokerTopicsRepository.TopicQuery.() -> Unit): List<LogTopic> = lock.withLock {
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