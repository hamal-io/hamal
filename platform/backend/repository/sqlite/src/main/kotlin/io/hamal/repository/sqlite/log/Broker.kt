package io.hamal.repository.sqlite.log

import io.hamal.lib.common.KeyedOnce
import io.hamal.lib.common.domain.*
import io.hamal.lib.domain.vo.LogTopicId
import io.hamal.repository.api.log.*
import io.hamal.repository.api.log.LogBrokerRepository.*
import java.nio.file.Path
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock


class LogBrokerSqliteRepository(
    val path: Path
) : LogBrokerRepository {

    override fun append(cmdId: CmdId, topicId: LogTopicId, bytes: ByteArray) {
        resolveRepository(topicId).append(cmdId, bytes)
    }

    override fun commit(consumerId: LogConsumerId, topicId: LogTopicId, eventId: LogEventId) {
        consumerRepository.commit(consumerId, topicId, eventId)
    }

    override fun consume(consumerId: LogConsumerId, topicId: LogTopicId, limit: Limit): List<LogEvent> {
        val nextEventId = consumerRepository.nextEventId(consumerId, topicId)
        return resolveRepository(topicId).read(nextEventId, limit)
    }


    override fun countConsumers(query: LogConsumerQuery): Count {
        return consumerRepository.count(query)
    }

    override fun listEvents(query: LogEventQuery): List<LogEvent> {
        TODO("Not yet implemented")
    }

    override fun countEvents(query: LogEventQuery): Count {
        TODO("Not yet implemented")
    }

    override fun create(cmd: CreateTopicCmd): LogTopic {
        return topicRepository.create(cmd)
    }

    override fun findTopic(topicId: LogTopicId): LogTopic? = topicRepository.find(topicId)

    override fun listTopics(query: LogTopicQuery): List<LogTopic> = topicRepository.list(query)

    override fun countTopics(query: LogTopicQuery): Count {
        return topicRepository.count(query)
    }

    override fun read(firstId: LogEventId, topicId: LogTopicId, limit: Limit): List<LogEvent> {
        return resolveRepository(topicId).read(firstId, limit)
    }

    override fun clear() {
        lock.withLock {
            topicRepository.clear()
            consumerRepository.clear()
            topicRepositories.keys().forEach { topic ->
                resolveRepository(topic).clear()
            }
        }
    }

    override fun close() {
        lock.withLock {
            topicRepository.close()
            consumerRepository.close()
            topicRepositories.keys().forEach { topic ->
                resolveRepository(topic).close()
            }
        }
    }

    private fun resolveRepository(topicId: LogTopicId) = topicRepositories(topicId) {
        LogTopicSqliteRepository(
            LogTopic(
                id = topicId,
                createdAt = CreatedAt.now(),
                updatedAt = UpdatedAt.now()
            ), path
        )
    }

    private val consumerRepository = LogBrokerConsumerSqliteRepository(path)
    private val topicRepository = LogBrokerTopicSqliteRepository(path)
    private val topicRepositories = KeyedOnce.default<LogTopicId, LogTopicSqliteRepository>()
    private val lock = ReentrantLock()
}


