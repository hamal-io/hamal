package io.hamal.repository.memory.log

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.repository.api.log.BrokerTopicsRepository
import io.hamal.repository.api.log.BrokerTopicsRepository.TopicQuery
import io.hamal.repository.api.log.BrokerTopicsRepository.TopicToCreate
import io.hamal.repository.api.log.Topic
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock


class MemoryBrokerTopicsRepository : BrokerTopicsRepository {

    private val lock = ReentrantLock()
    private val topicMapping = mutableMapOf<TopicName, Topic>()
    private val topics = mutableMapOf<TopicId, Topic>()

    override fun create(cmdId: CmdId, toCreate: TopicToCreate): Topic {
        return lock.withLock {
            require(topicMapping.values.none { it.id == toCreate.id }) { "Topic already exists" }
            require(!topicMapping.containsKey(toCreate.name)) { "Topic already exists" }
            topicMapping[toCreate.name] = Topic(
                id = toCreate.id,
                name = toCreate.name
            )
            topics[toCreate.id] = topicMapping[toCreate.name]!!
            topicMapping[toCreate.name]!!
        }
    }

    override fun find(name: TopicName): Topic? {
        return lock.withLock {
            topicMapping[name]
        }
    }

    override fun find(id: TopicId): Topic? = lock.withLock {
        topics[id]
    }

    override fun list(query: TopicQuery): List<Topic> {
        return lock.withLock {
            topics.entries.sortedBy { it.key }
                .reversed()
                .filter { if (query.names.isEmpty()) true else query.names.contains(it.value.name) }
                .dropWhile { it.key >= query.afterId }
                .take(query.limit.value)
                .map { it.value }
        }
    }

    override fun count(query: TopicQuery): ULong {
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