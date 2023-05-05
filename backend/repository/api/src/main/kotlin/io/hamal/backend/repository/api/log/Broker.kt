package io.hamal.backend.repository.api.log

import io.hamal.backend.repository.api.log.Consumer.GroupId
import java.io.Closeable
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
    fun append(topic: Topic, bytes: ByteArray)
}

interface ConsumeFromTopic {
    fun read(groupId: GroupId, topic: Topic, limit: Int): List<Chunk>

    fun commit(groupId: GroupId, topic: Topic, chunkId: Chunk.Id)
}

interface ResolveTopic {
    fun resolveTopic(topicName: Topic.Name): Topic
}

interface BrokerRepository : AppendToTopic, ConsumeFromTopic, ResolveTopic, Closeable