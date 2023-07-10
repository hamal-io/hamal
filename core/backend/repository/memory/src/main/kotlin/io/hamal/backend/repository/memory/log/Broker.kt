package io.hamal.backend.repository.memory.log

import io.hamal.backend.repository.api.log.*
import io.hamal.backend.repository.api.log.LogBrokerTopicsRepository.TopicToCreate
import io.hamal.lib.common.KeyedOnce
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName


class MemoryLogBrokerRepository : LogBrokerRepository<MemoryLogTopic> {

    private val consumersRepository: MemoryLogBrokerConsumersRepository = MemoryLogBrokerConsumersRepository()
    private val topicsRepository: MemoryLogBrokerTopicsRepository = MemoryLogBrokerTopicsRepository()

    private val repositoryMapping = KeyedOnce.default<MemoryLogTopic, LogTopicRepository>()

    override fun append(cmdId: CmdId, topic: MemoryLogTopic, bytes: ByteArray) {
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

    override fun consume(groupId: GroupId, topic: MemoryLogTopic, limit: Int): List<LogChunk> {
        val nextChunkId = consumersRepository.nextChunkId(groupId, topic.id)
        return resolveRepository(topic).read(nextChunkId, limit)
    }

    override fun commit(groupId: GroupId, topic: MemoryLogTopic, chunkId: LogChunkId) {
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

    override fun create(cmdId: CmdId, topicToCreate: CreateTopic.TopicToCreate): MemoryLogTopic =
        topicsRepository.create(
            cmdId,
            TopicToCreate(
                topicToCreate.id,
                topicToCreate.name
            )
        )

    override fun findTopic(topicId: TopicId) = topicsRepository.find(topicId)
    override fun findTopic(topicName: TopicName) = topicsRepository.find(topicName)
    override fun listTopics(): List<MemoryLogTopic> {
        return topicsRepository.list()
    }

    override fun read(firstId: LogChunkId, topic: MemoryLogTopic, limit: Int): List<LogChunk> {
        return resolveRepository(topic).read(firstId, limit)
    }

    private fun resolveRepository(topic: MemoryLogTopic) = repositoryMapping(topic) {
        MemoryLogTopicRepository(topic)
    }
}