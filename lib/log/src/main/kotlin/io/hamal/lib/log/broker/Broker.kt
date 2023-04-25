package io.hamal.lib.log.broker

import io.hamal.lib.log.consumer.Consumer.GroupId
import io.hamal.lib.log.segment.Chunk
import io.hamal.lib.log.topic.Topic
import io.hamal.lib.log.topic.TopicRepository
import io.hamal.lib.meta.KeyedOnce
import io.hamal.lib.util.Files
import java.nio.file.Path

data class Broker(
    val id: Id,
    val path: Path
) {
    @JvmInline
    value class Id(val value: ULong) {
        constructor(value: Int) : this(value.toULong())
    }
}

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

    private val topicRepositoryMapping = KeyedOnce.default<Topic, TopicRepository>()

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
        resolveRepository(topic).append(*bytes)
    }

    override fun close() {
        topicsRepository.close()
        consumersRepository.close()
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