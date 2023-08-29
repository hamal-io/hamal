package io.hamal.repository.sqlite.log

import io.hamal.repository.api.log.*
import io.hamal.repository.api.log.BrokerTopicsRepository.TopicQuery
import io.hamal.lib.common.KeyedOnce
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import java.nio.file.Path

data class SqliteLogBroker(
    val path: Path
)

class SqliteLogBrokerRepository(
    private val logBroker: SqliteLogBroker
) : LogBrokerRepository {

    private val consumersRepository: SqliteLogBrokerConsumersRepository
    private val topicsRepository: SqliteBrokerTopicsRepository

    init {
        topicsRepository = SqliteBrokerTopicsRepository(
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

    private val topicRepositoryMapping = KeyedOnce.default<LogTopic, LogTopicRepository>()


    override fun create(cmdId: CmdId, topicToCreate: CreateTopic.TopicToCreate): LogTopic =
        topicsRepository.create(
            cmdId,
            BrokerTopicsRepository.TopicToCreate(
                topicToCreate.id,
                topicToCreate.name
            )
        )

    override fun append(cmdId: CmdId, topic: LogTopic, bytes: ByteArray) {
        resolveRepository(topic).append(cmdId, bytes)
    }

    override fun close() {
        topicsRepository.close()
        consumersRepository.close()
        topicRepositoryMapping.keys().forEach { topic ->
            resolveRepository(topic).close()
        }
    }

    override fun consume(groupId: GroupId, topic: LogTopic, limit: Int): List<LogChunk> {
        val nextChunkId = consumersRepository.nextChunkId(groupId, topic.id)
        return resolveRepository(topic).read(nextChunkId, limit)
    }

    override fun commit(groupId: GroupId, topic: LogTopic, chunkId: LogChunkId) {
        consumersRepository.commit(groupId, topic.id, chunkId)
    }

    override fun findTopic(topicId: TopicId) = topicsRepository.find(topicId)
    override fun findTopic(topicName: TopicName) = topicsRepository.find(topicName)
    override fun listTopics(block: TopicQuery.() -> Unit): List<LogTopic> {
        return topicsRepository.list(block)
    }

    override fun clear() {
        topicsRepository.clear()
        consumersRepository.clear()
        topicRepositoryMapping.keys().forEach { topic ->
            resolveRepository(topic).clear()
        }
    }


    override fun read(firstId: LogChunkId, topic: LogTopic, limit: Int): List<LogChunk> {
        return resolveRepository(topic).read(firstId, limit)
    }

    private fun resolveRepository(topic: LogTopic) = topicRepositoryMapping(topic) {
        SqliteLogTopicRepository(topic, path = logBroker.path)
    }
}