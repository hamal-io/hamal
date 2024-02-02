package io.hamal.repository.memory.new_log

import io.hamal.lib.common.KeyedOnce
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.repository.api.new_log.*
import io.hamal.repository.api.new_log.LogBrokerRepository.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class LogBrokerMemoryRepository : LogBrokerRepository {

    override fun append(cmdId: CmdId, topicId: LogTopicId, bytes: ByteArray) {
        resolveTopicRepository(topicId).append(cmdId, bytes)
    }

    override fun commit(consumerId: LogConsumerId, topicId: LogTopicId, entryId: LogEntryId) {
        return lock.withLock {
            consumers.putIfAbsent(Pair(consumerId, topicId), LogEntryId(0))
            consumers[Pair(consumerId, topicId)] = LogEntryId(entryId.value.toInt() + 1)
            consumers[Pair(consumerId, topicId)]
        }
    }

    override fun consume(consumerId: LogConsumerId, topicId: LogTopicId, limit: Limit): List<LogEntry> {
        return lock.withLock {
            val nextEntryId = nextEntryId(consumerId, topicId)
            resolveTopicRepository(topicId).read(nextEntryId, limit)
        }
    }

    override fun countTopics(query: LogTopicQuery): ULong {
        return lock.withLock {
            topics.filter { query.groupIds.isEmpty() || it.key in query.groupIds }
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

    override fun countConsumers(query: LogConsumerQuery): ULong {
        return lock.withLock {
            consumers.values.size.toULong()
        }
    }

    override fun create(cmdId: CmdId, topicToCreate: LogTopicToCreate): LogTopic {
        return lock.withLock {
            val groupId = topicToCreate.groupId
            topics.putIfAbsent(groupId, mutableMapOf())

            val topic = LogTopicMemory(
                id = topicToCreate.id,
                name = topicToCreate.name,
                groupId = topicToCreate.groupId
            )

            require(resolveTopic(groupId, topic.name) == null) { "Topic already exists" }
            require(findTopic(topic.id) == null) { "Topic already exists" }

            topics[groupId]!![topic.id] = topic

            topic
        }
    }

    override fun findTopic(topicId: LogTopicId): LogTopic? {
        return lock.withLock {
            topics.entries.asSequence()
                .map { it.value }
                .flatMap { it.values }
                .find { it.id == topicId }
        }
    }


    override fun listTopics(query: LogTopicQuery): List<LogTopic> {
        return lock.withLock {
            topics.filter { query.groupIds.isEmpty() || it.key in query.groupIds }
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

    override fun read(firstId: LogEntryId, topicId: LogTopicId, limit: Limit): List<LogEntry> {
        TODO("Not yet implemented")
    }

    override fun resolveTopic(groupId: LogTopicGroupId, name: LogTopicName): LogTopic? {
        return lock.withLock {
            (topics[groupId] ?: emptyMap()).values.find { it.name == name }
        }
    }

    override fun clear() {
        topics.clear()
        consumers.clear()
    }

    override fun close() {
        clear()
    }

    internal fun nextEntryId(consumerId: LogConsumerId, topicId: LogTopicId): LogEntryId {
        return lock.withLock {
            consumers[Pair(consumerId, topicId)] ?: LogEntryId(0)
        }
    }

    internal fun resolveTopicRepository(topicId: LogTopicId): LogTopicRepository {
        return topicRepositories(topicId) {
            val topic = getTopic(topicId)
            LogTopicMemoryRepository(topic)
        }
    }

    private val lock = ReentrantLock()
    private val topicRepositories = KeyedOnce.default<LogTopicId, LogTopicRepository>()
    private val topics = mutableMapOf<LogTopicGroupId, MutableMap<LogTopicId, LogTopicMemory>>()
    private val consumers = mutableMapOf<Pair<LogConsumerId, LogTopicId>, LogEntryId>()
}