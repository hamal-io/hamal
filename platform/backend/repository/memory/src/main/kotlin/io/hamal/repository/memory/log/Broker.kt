package io.hamal.repository.memory.log

import io.hamal.lib.common.KeyedOnce
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.FlowId
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

    override fun consume(consumerId: ConsumerId, topic: Topic, limit: Int): List<Chunk> {
        val nextChunkId = consumersRepository.nextChunkId(consumerId, topic.id)
        return resolveRepository(topic).read(nextChunkId, limit)
    }

    override fun commit(consumerId: ConsumerId, topic: Topic, chunkId: ChunkId) {
        consumersRepository.commit(consumerId, topic.id, chunkId)
    }

    override fun clear() {
        topicsRepository.clear()
        consumersRepository.clear()
        repositoryMapping.keys()
            .forEach { topic ->
                resolveRepository(topic).clear()
            }
    }

    override fun create(cmdId: CmdId, topicToCreate: CreateTopic.TopicToCreate) =
        topicsRepository.create(
            cmdId,
            TopicToCreate(
                id = topicToCreate.id,
                name = topicToCreate.name,
                flowId = topicToCreate.flowId,
                groupId = topicToCreate.groupId
            )
        )

    override fun findTopic(topicId: TopicId) = topicsRepository.find(topicId)
    override fun findTopic(flowId: FlowId, topicName: TopicName) = topicsRepository.find(flowId, topicName)
    override fun listTopics(query: TopicQuery): List<Topic> {
        return topicsRepository.list(query)
    }

    override fun resolveTopic(flowId: FlowId, name: TopicName) = topicsRepository.find(flowId, name)

    override fun read(firstId: ChunkId, topic: Topic, limit: Int): List<Chunk> {
        return resolveRepository(topic).read(firstId, limit)
    }

    private fun resolveRepository(topic: Topic) = repositoryMapping(topic) {
        MemoryTopicRepository(topic)
    }
}