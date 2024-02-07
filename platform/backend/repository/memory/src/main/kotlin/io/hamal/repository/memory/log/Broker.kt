package io.hamal.repository.memory.log

import io.hamal.lib.common.KeyedOnce
import io.hamal.lib.common.domain.*
import io.hamal.lib.domain.vo.LogTopicId
import io.hamal.repository.api.log.*
import io.hamal.repository.api.log.LogBrokerRepository.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class LogBrokerMemoryRepository : LogBrokerRepository {

    override fun append(cmdId: CmdId, topicId: LogTopicId, bytes: ByteArray) {
        resolveTopicRepository(topicId).append(cmdId, bytes)
    }

    override fun commit(consumerId: LogConsumerId, topicId: LogTopicId, eventId: LogEventId) {
        return lock.withLock {
            consumers.putIfAbsent(Pair(consumerId, topicId), LogEventId(0))
            consumers[Pair(consumerId, topicId)] = LogEventId(eventId.value.toInt() + 1)
            consumers[Pair(consumerId, topicId)]
        }
    }

    override fun consume(consumerId: LogConsumerId, topicId: LogTopicId, limit: Limit): List<LogEvent> {
        return lock.withLock {
            val nextEntryId = nextEventId(consumerId, topicId)
            resolveTopicRepository(topicId).read(nextEntryId, limit)
        }
    }

    override fun countTopics(query: LogTopicQuery): Count {
        return lock.withLock {
            Count(
                topics.values
                    .sortedBy { it.id }
                    .reversed()
                    .asSequence()
                    .dropWhile { it.id >= query.afterId }
                    .count()
            )
        }
    }

    override fun countConsumers(query: LogConsumerQuery): Count {
        return lock.withLock {
            Count(consumers.values.size)
        }
    }

    override fun listEvents(query: LogEventQuery): List<LogEvent> {
        TODO("Not yet implemented")
    }

    override fun countEvents(query: LogEventQuery): Count {
        TODO("Not yet implemented")
    }

    override fun create(cmdId: CmdId, topicToCreate: LogTopicToCreate): LogTopic {
        return lock.withLock {
            val topic = LogTopic(topicToCreate.id, CreatedAt.now(), UpdatedAt.now())
            require(findTopic(topic.id) == null) { "Topic already exists" }
            topics[topic.id] = topic
            topic
        }
    }

    override fun findTopic(topicId: LogTopicId): LogTopic? {
        return lock.withLock {
            topics[topicId]
        }
    }


    override fun listTopics(query: LogTopicQuery): List<LogTopic> {
        return lock.withLock {
            topics.values.sortedBy { it.id }
                .reversed()
                .asSequence()
                .dropWhile { it.id >= query.afterId }
                .take(query.limit.value)
                .toList()
        }
    }

    override fun read(firstId: LogEventId, topicId: LogTopicId, limit: Limit): List<LogEvent> {
        return lock.withLock {
            resolveTopicRepository(topicId).read(firstId, limit)
        }
    }

    override fun clear() {
        topics.clear()
        consumers.clear()
    }

    override fun close() {
        clear()
    }

    internal fun nextEventId(consumerId: LogConsumerId, topicId: LogTopicId): LogEventId {
        return lock.withLock {
            consumers[Pair(consumerId, topicId)] ?: LogEventId(0)
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
    private val topics = mutableMapOf<LogTopicId, LogTopic>()
    private val consumers = mutableMapOf<Pair<LogConsumerId, LogTopicId>, LogEventId>()
}