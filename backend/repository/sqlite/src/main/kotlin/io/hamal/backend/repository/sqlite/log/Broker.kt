package io.hamal.backend.repository.sqlite.log

import io.hamal.backend.repository.api.log.*
import io.hamal.lib.common.KeyedOnce
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import java.nio.file.Path

data class SqliteLogBroker(
    override val id: LogBroker.Id,
    val path: Path
) : LogBroker


class SqliteLogBrokerRepository(
    logBroker: SqliteLogBroker
) : LogBrokerRepository<SqliteLogTopic> {

    private val consumersRepository: SqliteLogBrokerConsumersRepository
    private val topicsRepository: SqliteLogBrokerTopicsRepository

    init {
        topicsRepository = SqliteLogBrokerTopicsRepository(
            SqliteBrokerTopics(
                logBrokerId = logBroker.id,
                path = logBroker.path
            )
        )
        consumersRepository = SqliteLogBrokerConsumersRepository(
            SqliteBrokerConsumers(
                logBrokerId = logBroker.id,
                path = logBroker.path
            )
        )
    }

    private val logTopicRepositoryMapping = KeyedOnce.default<SqliteLogTopic, LogTopicRepository>()

    override fun resolveTopic(topicName: TopicName) = topicsRepository.resolveTopic(topicName)

    override fun append(cmdId: CmdId, topic: SqliteLogTopic, bytes: ByteArray) {
        resolveRepository(topic).append(cmdId, bytes)
    }

    override fun close() {
        topicsRepository.close()
        consumersRepository.close()
        logTopicRepositoryMapping.keys().forEach { topic ->
            resolveRepository(topic).close()
        }
    }

    override fun consume(groupId: GroupId, topic: SqliteLogTopic, limit: Int): List<LogChunk> {
        val nextChunkId = consumersRepository.nextChunkId(groupId, topic.id)
        return resolveRepository(topic).read(nextChunkId, limit)
    }

    override fun commit(groupId: GroupId, topic: SqliteLogTopic, chunkId: LogChunkId) {
        consumersRepository.commit(groupId, topic.id, chunkId)
    }

    override fun find(topicId: TopicId) = topicsRepository.find(topicId)

    override fun topics(): Set<SqliteLogTopic> {
        return logTopicRepositoryMapping.keys()
    }

    override fun read(lastId: LogChunkId, topic: SqliteLogTopic, limit: Int): List<LogChunk> {
        return resolveRepository(topic).read(lastId, limit)
    }

    private fun resolveRepository(topic: SqliteLogTopic) = logTopicRepositoryMapping(topic) {
        SqliteLogTopicRepository(topic)
    }
}