package io.hamal.backend.repository.impl.log

import io.hamal.backend.repository.api.log.Broker
import io.hamal.backend.repository.api.log.Chunk
import io.hamal.backend.repository.api.log.Topic
import io.hamal.backend.repository.impl.log.Consumer.GroupId
import io.hamal.lib.util.Files
import java.nio.file.Path


interface AppendToTopic {
    fun append(topic: Topic, vararg bytes: ByteArray)
}

interface ConsumeFromTopic {
    fun read(groupId: GroupId, topic: Topic, limit: Int): List<Chunk>

    fun commit(groupId: GroupId, topic: Topic, chunkId: Chunk.Id)
}

interface ResolveTopic {
    fun resolveTopic(topicName: Topic.Name): Topic
}

class BrokerRepository private constructor(
    internal val topicsRepository: BrokerTopicsRepository,
    internal val consumersRepository: BrokerConsumersRepository,
) : AppendToTopic, ConsumeFromTopic, AutoCloseable, ResolveTopic {

    private val topicRepositoryMapping = io.hamal.lib.KeyedOnce.default<Topic, TopicRepository>()

    companion object {
        fun open(broker: Broker): BrokerRepository {
            val path = ensureDirectoryExists(broker)
            return BrokerRepository(
                topicsRepository = BrokerTopicsRepository.open(
                    BrokerTopics(
                        brokerId = broker.id,
                        path = path
                    )
                ),
                consumersRepository = BrokerConsumersRepository.open(
                    BrokerConsumers(
                        brokerId = broker.id,
                        path = path
                    )
                )
            )
        }

        private fun ensureDirectoryExists(broker: Broker): Path {
            return Files.createDirectories(broker.path)
        }
    }

    override fun resolveTopic(topicName: Topic.Name) = topicsRepository.resolveTopic(topicName)

    override fun append(topic: Topic, vararg bytes: ByteArray) {
        TODO()
//        resolveRepository(topic).append(*bytes)
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

    private fun resolveRepository(topic: Topic) = topicRepositoryMapping(topic) { TopicRepository.open(topic) }
}