package io.hamal.repository.sqlite.log

import io.hamal.lib.common.KeyedOnce
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.repository.api.log.*
import io.hamal.repository.api.log.BrokerTopicsRepository.TopicQuery
import io.hamal.repository.api.log.BrokerTopicsRepository.TopicToCreate
import java.nio.file.Path

data class SqliteBroker(
    val path: Path
)

class SqliteBrokerRepository(
    private val broker: SqliteBroker
) : BrokerRepository {

    private val consumersRepository: SqliteBrokerConsumersRepository
    private val topicsRepository: SqliteBrokerTopicsRepository

    init {
        topicsRepository = SqliteBrokerTopicsRepository(
            SqliteBrokerTopics(
                path = broker.path
            )
        )
        consumersRepository = SqliteBrokerConsumersRepository(
            SqliteBrokerConsumers(
                path = broker.path
            )
        )
    }

    private val topicRepositoryMapping = KeyedOnce.default<Topic, TopicRepository>()


    override fun create(cmdId: CmdId, topicToCreate: CreateTopic.TopicToCreate): Topic =
        topicsRepository.create(
            cmdId,
            TopicToCreate(
                topicToCreate.id,
                topicToCreate.name
            )
        )

    override fun append(cmdId: CmdId, topic: Topic, bytes: ByteArray) {
        resolveRepository(topic).append(cmdId, bytes)
    }

    override fun close() {
        topicsRepository.close()
        consumersRepository.close()
        topicRepositoryMapping.keys().forEach { topic ->
            resolveRepository(topic).close()
        }
    }

    override fun consume(groupId: GroupId, topic: Topic, limit: Int): List<Chunk> {
        val nextChunkId = consumersRepository.nextChunkId(groupId, topic.id)
        return resolveRepository(topic).read(nextChunkId, limit)
    }

    override fun commit(groupId: GroupId, topic: Topic, chunkId: ChunkId) {
        consumersRepository.commit(groupId, topic.id, chunkId)
    }

    override fun findTopic(topicId: TopicId) = topicsRepository.find(topicId)
    override fun findTopic(topicName: TopicName) = topicsRepository.find(topicName)
    override fun listTopics(block: TopicQuery.() -> Unit): List<Topic> {
        return topicsRepository.list(block)
    }

    override fun clear() {
        topicsRepository.clear()
        consumersRepository.clear()
        topicRepositoryMapping.keys().forEach { topic ->
            resolveRepository(topic).clear()
        }
    }


    override fun read(firstId: ChunkId, topic: Topic, limit: Int): List<Chunk> {
        return resolveRepository(topic).read(firstId, limit)
    }

    private fun resolveRepository(topic: Topic) = topicRepositoryMapping(topic) {
        SqliteTopicRepository(topic, path = broker.path)
    }
}