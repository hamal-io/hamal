package io.hamal.repository.memory.log

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.FlowId
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
    private val topics = mutableMapOf<FlowId, MutableMap<TopicId, Topic>>()

    override fun create(cmdId: CmdId, toCreate: TopicToCreate): Topic {
        return lock.withLock {
            val flowId = toCreate.flowId
            topics.putIfAbsent(flowId, mutableMapOf())

            val topic = Topic(
                id = toCreate.id,
                name = toCreate.name,
                flowId = toCreate.flowId,
                groupId = toCreate.groupId
            )

            require(find(flowId, topic.name) == null) { "Topic already exists" }
            require(find(topic.id) == null) { "Topic already exists" }

            topics[flowId]!![topic.id] = topic

            topic
        }
    }

    override fun find(flowId: FlowId, name: TopicName): Topic? {
        return lock.withLock {
            (topics[flowId] ?: emptyMap()).values.find { it.name == name }
        }
    }

    override fun find(id: TopicId): Topic? = lock.withLock {
        topics.entries.asSequence()
            .map { it.value }
            .flatMap { it.values }
            .find { it.id == id }
    }

    override fun list(query: TopicQuery): List<Topic> {
        return lock.withLock {
            topics.filter { query.flowIds.isEmpty() || it.key in query.flowIds }
                .flatMap { it.value.values }
                .reversed()
                .asSequence()
                .filter { if (query.names.isEmpty()) true else query.names.contains(it.name) }
                .filter { if (query.groupIds.isEmpty()) true else query.groupIds.contains(it.groupId) }
                .dropWhile { it.id >= query.afterId }
                .take(query.limit.value)
                .toList()
        }
    }

    override fun count(query: TopicQuery): ULong {
        return lock.withLock {
            topics.filter { query.flowIds.isEmpty() || it.key in query.flowIds }
                .flatMap { it.value.values }
                .reversed()
                .asSequence()
                .filter { if (query.names.isEmpty()) true else query.names.contains(it.name) }
                .filter { if (query.groupIds.isEmpty()) true else query.groupIds.contains(it.groupId) }
                .dropWhile { it.id >= query.afterId }
                .count()
                .toULong()
        }
    }

    override fun clear() {
        topics.clear()
    }

    override fun close() {
    }
}