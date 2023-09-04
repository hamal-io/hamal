package io.hamal.repository.memory.log

import io.hamal.lib.common.KeyedOnce
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.repository.api.log.*
import io.hamal.repository.api.log.BrokerTopicsRepository.TopicQuery
import io.hamal.repository.api.log.BrokerTopicsRepository.TopicToCreate


class MemoryBrokerRepository : BrokerRepository {

    private val consumersRepository: MemoryBrokerConsumersRepository = MemoryBrokerConsumersRepository()
    private val topicsRepository: MemoryBrokerTopicsRepository = MemoryBrokerTopicsRepository()

    private val repositoryMapping = KeyedOnce.default<Topic, TopicRepository>()

    override fun append(cmdId: CmdId, topic: Topic, bytes: ByteArray) {
        resolveRepository(topic).append(cmdId, bytes)
    }

    override fun close() {
        topicsRepository.close()
        consumersRepository.close()
        repositoryMapping.keys()
            .forEach { topic ->
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

    override fun clear() {
        topicsRepository.clear()
        consumersRepository.clear()
        repositoryMapping.keys()
            .forEach { topic ->
                resolveRepository(topic).clear()
            }
    }

    override fun create(cmdId: CmdId, topicToCreate: CreateTopic.TopicToCreate): Topic =
        topicsRepository.create(
            cmdId,
            TopicToCreate(
                topicToCreate.id,
                topicToCreate.name
            )
        )

    override fun findTopic(topicId: TopicId) = topicsRepository.find(topicId)
    override fun findTopic(topicName: TopicName) = topicsRepository.find(topicName)
    override fun listTopics(block: TopicQuery.() -> Unit): List<Topic> {
        return topicsRepository.list(block)
    }

    override fun read(firstId: ChunkId, topic: Topic, limit: Int): List<Chunk> {
        return resolveRepository(topic).read(firstId, limit)
    }

    private fun resolveRepository(topic: Topic) = repositoryMapping(topic) {
        MemoryTopicRepository(topic)
    }
}