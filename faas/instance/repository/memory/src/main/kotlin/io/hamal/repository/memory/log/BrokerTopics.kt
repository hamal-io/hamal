package io.hamal.repository.memory.log

import io.hamal.backend.repository.api.log.BrokerTopicsRepository
import io.hamal.backend.repository.api.log.BrokerTopicsRepository.TopicQuery
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
    private val topics = mutableMapOf<TopicId, LogTopic>()

    override fun create(cmdId: CmdId, toCreate: TopicToCreate): LogTopic {
        return lock.withLock {
            require(topicMapping.values.none { it.id == toCreate.id }) { "Topic already exists" }
            require(!topicMapping.containsKey(toCreate.name)) { "Topic already exists" }
            topicMapping[toCreate.name] = LogTopic(
                id = toCreate.id,
                name = toCreate.name
            )
            topics[toCreate.id] = topicMapping[toCreate.name]!!
            topicMapping[toCreate.name]!!
        }
    }

    override fun find(name: TopicName): LogTopic? {
        return lock.withLock {
            topicMapping[name]
        }
    }

    override fun find(id: TopicId): LogTopic? = lock.withLock {
        topics[id]
    }

    override fun list(block: TopicQuery.() -> Unit): List<LogTopic> {
        val query = TopicQuery().also(block)
        return lock.withLock {
            topics.entries.sortedBy { it.key }
                .reversed()
                .filter { if (query.names.isEmpty()) true else query.names.contains(it.value.name) }
                .dropWhile { it.key >= query.afterId }
                .take(query.limit.value)
                .map { it.value }
        }
    }

    override fun count(block: TopicQuery.() -> Unit): ULong {
        val query = TopicQuery().also(block)
        return lock.withLock {
            topics.entries.sortedBy { it.key }
                .reversed()
                .filter { if (query.names.isEmpty()) true else query.names.contains(it.value.name) }
                .dropWhile { it.key >= query.afterId }
                .count()
                .toULong()
        }
    }

    fun clear() {
        topicMapping.clear()
        topics.clear()
    }

    override fun close() {
    }
}