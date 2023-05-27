package io.hamal.backend.repository.api.log

import io.hamal.backend.repository.api.log.Consumer.GroupId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
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
    fun resolveTopic(topicName: TopicName): Topic
}

interface GetTopics {
    fun topics(): Set<Topic>
}

interface GetTopic {
    fun get(topicId: TopicId): Topic = requireNotNull(find(topicId)) { "Topic with id $topicId not found" }

    fun find(topicId: TopicId): Topic?
}

interface BrokerRepository : AppendToTopic, ConsumeFromTopic, GetTopic, GetTopics, ResolveTopic, Closeable