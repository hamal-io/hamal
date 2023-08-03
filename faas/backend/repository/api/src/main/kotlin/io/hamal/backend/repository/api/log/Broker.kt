package io.hamal.backend.repository.api.log

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.Event
import io.hamal.lib.domain.EventWithId
import io.hamal.lib.domain.vo.EventId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.Closeable

interface CreateTopic<TOPIC : LogTopic> {
    fun create(cmdId: CmdId, topicToCreate: TopicToCreate): TOPIC

    data class TopicToCreate(
        val id: TopicId,
        val name: TopicName
    )
}

interface AppendToTopic<TOPIC : LogTopic> {
    fun append(cmdId: CmdId, topic: TOPIC, bytes: ByteArray)
}

interface ConsumeFromTopic<TOPIC : LogTopic> {
    fun consume(groupId: GroupId, topic: TOPIC, limit: Int): List<LogChunk>

    fun commit(groupId: GroupId, topic: TOPIC, chunkId: LogChunkId)
}

interface ReadFromTopic<TOPIC : LogTopic> {
    fun read(firstId: LogChunkId, topic: TOPIC, limit: Int): List<LogChunk>
}

interface FindTopic<TOPIC : LogTopic> {
    fun getTopic(topicId: TopicId): TOPIC = findTopic(topicId) ?: throw NoSuchElementException("Topic not found")

    fun findTopic(topicId: TopicId): TOPIC?

    fun findTopic(topicName: TopicName): TOPIC?
}

interface LogBrokerRepository<TOPIC : LogTopic> :
    CreateTopic<TOPIC>,
    AppendToTopic<TOPIC>,
    ConsumeFromTopic<TOPIC>,
    FindTopic<TOPIC>,
    ReadFromTopic<TOPIC>,
    Closeable {

    fun listTopics(): List<TOPIC>

    @OptIn(ExperimentalSerializationApi::class)
    fun listEvents(topic: TOPIC, block: EventQuery.() -> Unit): List<EventWithId> {
        val query = EventQuery().also(block)
        val firstId = LogChunkId(SnowflakeId(query.afterId.value.value + 1))
        return read(firstId, topic, query.limit.value)
            .map { chunk ->
                val evt = ProtoBuf.decodeFromByteArray(Event.serializer(), chunk.bytes)
                EventWithId(
                    id = EventId(chunk.id.value),
                    value = evt.value
                )
            }
    }

    fun clear()

    data class EventQuery(
        var afterId: EventId = EventId(0),
        var limit: Limit = Limit(1)
    )
}