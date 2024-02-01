package io.hamal.repository.memory.new_log

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.repository.api.new_log.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class LogBrokerMemoryRepository : LogBrokerRepository {

    override fun append(cmdId: CmdId, topicId: LogTopicId, bytes: ByteArray) {
        TODO("Not yet implemented")
    }

    override fun commit(consumerId: LogTopicConsumerId, topicId: LogTopicId, entryId: LogEntryId) {
        TODO("Not yet implemented")
    }

    override fun consume(consumerId: LogTopicConsumerId, topicId: LogTopicId, limit: Limit): List<LogEntry> {
        TODO("Not yet implemented")
    }

    override fun count(query: LogTopicQuery): ULong {
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

    override fun create(cmdId: CmdId, topicToCreate: LogTopicCreate.ToCreate): LogTopic {
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
        return topics.entries.asSequence()
            .map { it.value }
            .flatMap { it.values }
            .find { it.id == topicId }
    }

    override fun list(query: LogTopicQuery): List<LogTopic> {
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
    }

    override fun close() {
        clear()
    }

    private val lock = ReentrantLock()
    private val topics = mutableMapOf<LogTopicGroupId, MutableMap<LogTopicId, LogTopicMemory>>()
}