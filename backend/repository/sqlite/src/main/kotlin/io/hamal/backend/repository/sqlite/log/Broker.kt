package io.hamal.backend.repository.sqlite.log

import io.hamal.backend.repository.api.log.*
import io.hamal.backend.repository.api.log.Consumer.GroupId
import io.hamal.lib.common.KeyedOnce
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName


class DefaultBrokerRepository(
    private val broker: Broker
) : BrokerRepository {

    internal val consumersRepository: BrokerConsumersRepository
    internal val topicsRepository: BrokerTopicsRepository

    init {
        topicsRepository = DefaultBrokerTopicsRepository(
            BrokerTopics(
                brokerId = broker.id,
                path = broker.path
            )
        )
        consumersRepository = DefaultBrokerConsumersRepository(
            BrokerConsumers(
                brokerId = broker.id,
                path = broker.path
            )
        )
    }

    private val topicRepositoryMapping = KeyedOnce.default<Topic, TopicRepository>()

    override fun resolveTopic(topicName: TopicName) = topicsRepository.resolveTopic(topicName)

    override fun append(topic: Topic, bytes: ByteArray) {
        resolveRepository(topic).append(bytes)
    }

    override fun close() {
        topicsRepository.close()
        consumersRepository.close()
        topicRepositoryMapping.keys().forEach { topic ->
            resolveRepository(topic).close()
        }
    }

    override fun read(groupId: GroupId, topic: Topic, limit: Int): List<Chunk> {
        val nextChunkId = consumersRepository.nextChunkId(groupId, topic.id)
        return resolveRepository(topic).read(nextChunkId, limit)
    }

    override fun commit(groupId: GroupId, topic: Topic, chunkId: Chunk.Id) {
        consumersRepository.commit(groupId, topic.id, chunkId)
    }

    override fun find(topicId: TopicId) = topicsRepository.find(topicId)

    override fun topics(): Set<Topic> {
        return topicRepositoryMapping.keys()
    }

    private fun resolveRepository(topic: Topic) = topicRepositoryMapping(topic) {
        DefaultTopicRepository(topic)
    }
}