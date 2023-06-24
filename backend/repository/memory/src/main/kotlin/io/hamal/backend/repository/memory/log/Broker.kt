package io.hamal.backend.repository.memory.log

import io.hamal.backend.repository.api.log.*
import io.hamal.lib.common.KeyedOnce
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName


class MemoryLogBrokerRepository : LogBrokerRepository<MemoryLogTopic> {

    private val consumersRepository: MemoryLogBrokerConsumersRepository
    private val topicsRepository: MemoryLogBrokerTopicsRepository

    init {
        topicsRepository = MemoryLogBrokerTopicsRepository()
        consumersRepository = MemoryLogBrokerConsumersRepository()
    }

    private val logTopicRepositoryMapping = KeyedOnce.default<MemoryLogTopic, LogTopicRepository>()


    override fun append(cmdId: CmdId, topic: MemoryLogTopic, bytes: ByteArray) {
        resolveRepository(topic).append(cmdId, bytes)
    }

    override fun close() {
        topicsRepository.close()
        consumersRepository.close()
        logTopicRepositoryMapping.keys().forEach { topic ->
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

    override fun create(cmdId: CmdId, topicToCreate: CreateTopic.TopicToCreate): MemoryLogTopic =
        topicsRepository.create(
            cmdId, LogBrokerTopicsRepository.TopicToCreate(
                topicToCreate.id,
                topicToCreate.name
            )
        )

    override fun find(topicId: TopicId) = topicsRepository.find(topicId)
    override fun find(topicName: TopicName) = topicsRepository.find(topicName)

    override fun topics(): Set<MemoryLogTopic> {
        return logTopicRepositoryMapping.keys()
    }

    override fun read(lastId: LogChunkId, topic: MemoryLogTopic, limit: Int): List<LogChunk> {
        return resolveRepository(topic).read(lastId, limit)
    }

    private fun resolveRepository(topic: MemoryLogTopic) = logTopicRepositoryMapping(topic) {
        MemoryLogTopicRepository(topic)
    }
}