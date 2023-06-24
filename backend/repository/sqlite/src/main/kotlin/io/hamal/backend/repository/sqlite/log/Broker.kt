package io.hamal.backend.repository.sqlite.log

import io.hamal.backend.repository.api.log.*
import io.hamal.lib.common.KeyedOnce
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import java.nio.file.Path

data class SqliteLogBroker(
    val path: Path
)

class SqliteLogBrokerRepository(
    logBroker: SqliteLogBroker
) : LogBrokerRepository<SqliteLogTopic> {

    private val consumersRepository: SqliteLogBrokerConsumersRepository
    private val topicsRepository: SqliteLogBrokerTopicsRepository

    init {
        topicsRepository = SqliteLogBrokerTopicsRepository(
            SqliteBrokerTopics(
                path = logBroker.path
            )
        )
        consumersRepository = SqliteLogBrokerConsumersRepository(
            SqliteBrokerConsumers(
                path = logBroker.path
            )
        )
    }

    private val topicRepositoryMapping = KeyedOnce.default<SqliteLogTopic, LogTopicRepository>()

    override fun create(cmdId: CmdId, topicToCreate: CreateTopic.TopicToCreate): SqliteLogTopic =
        topicsRepository.create(
            cmdId,
            LogBrokerTopicsRepository.TopicToCreate(
                topicToCreate.id,
                topicToCreate.name
            )
        )

    override fun append(cmdId: CmdId, topic: SqliteLogTopic, bytes: ByteArray) {
        resolveRepository(topic).append(cmdId, bytes)
    }

    override fun close() {
        topicsRepository.close()
        consumersRepository.close()
        topicRepositoryMapping.keys().forEach { topic ->
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
    override fun find(topicName: TopicName) = topicsRepository.find(topicName)
    override fun queryTopics(): List<SqliteLogTopic> {
        return topicsRepository.query()
    }

    override fun read(lastId: LogChunkId, topic: SqliteLogTopic, limit: Int): List<LogChunk> {
        return resolveRepository(topic).read(lastId, limit)
    }

    private fun resolveRepository(topic: SqliteLogTopic) = topicRepositoryMapping(topic) {
        SqliteLogTopicRepository(topic)
    }
}