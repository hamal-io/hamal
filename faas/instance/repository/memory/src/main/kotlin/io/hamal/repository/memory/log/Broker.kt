package io.hamal.repository.memory.log

import io.hamal.repository.api.log.*
import io.hamal.repository.api.log.BrokerTopicsRepository.TopicQuery
import io.hamal.repository.api.log.BrokerTopicsRepository.TopicToCreate
import io.hamal.lib.common.KeyedOnce
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName


class MemoryLogBrokerRepository : LogBrokerRepository {

    private val consumersRepository: MemoryLogBrokerConsumersRepository = MemoryLogBrokerConsumersRepository()
    private val topicsRepository: MemoryBrokerTopicsRepository = MemoryBrokerTopicsRepository()

    private val repositoryMapping = KeyedOnce.default<LogTopic, LogTopicRepository>()

    override fun append(cmdId: CmdId, topic: LogTopic, bytes: ByteArray) {
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

    override fun consume(groupId: GroupId, topic: LogTopic, limit: Int): List<LogChunk> {
        val nextChunkId = consumersRepository.nextChunkId(groupId, topic.id)
        return resolveRepository(topic).read(nextChunkId, limit)
    }

    override fun commit(groupId: GroupId, topic: LogTopic, chunkId: LogChunkId) {
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

    override fun create(cmdId: CmdId, topicToCreate: CreateTopic.TopicToCreate): LogTopic =
        topicsRepository.create(
            cmdId,
            TopicToCreate(
                topicToCreate.id,
                topicToCreate.name
            )
        )

    override fun findTopic(topicId: TopicId) = topicsRepository.find(topicId)
    override fun findTopic(topicName: TopicName) = topicsRepository.find(topicName)
    override fun listTopics(block: TopicQuery.() -> Unit): List<LogTopic> {
        return topicsRepository.list(block)
    }

    override fun read(firstId: LogChunkId, topic: LogTopic, limit: Int): List<LogChunk> {
        return resolveRepository(topic).read(firstId, limit)
    }

    private fun resolveRepository(topic: LogTopic) = repositoryMapping(topic) {
        MemoryLogTopicRepository(topic)
    }
}