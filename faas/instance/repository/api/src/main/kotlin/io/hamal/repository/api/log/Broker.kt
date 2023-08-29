package io.hamal.repository.api.log

import io.hamal.backend.repository.api.log.BrokerTopicsRepository.TopicQuery
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

interface CreateTopic {
    fun create(cmdId: CmdId, topicToCreate: TopicToCreate): LogTopic

    data class TopicToCreate(
        val id: TopicId,
        val name: TopicName
    )
}

interface AppendToTopic {
    fun append(cmdId: CmdId, topic: LogTopic, bytes: ByteArray)
}

interface ConsumeFromTopic {
    fun consume(groupId: GroupId, topic: LogTopic, limit: Int): List<LogChunk>

    fun commit(groupId: GroupId, topic: LogTopic, chunkId: LogChunkId)
}

interface ReadFromTopic {
    fun read(firstId: LogChunkId, topic: LogTopic, limit: Int): List<LogChunk>
}

interface FindTopic {
    fun getTopic(topicId: TopicId): LogTopic = findTopic(topicId) ?: throw NoSuchElementException("Topic not found")

    fun findTopic(topicId: TopicId): LogTopic?

    fun findTopic(topicName: TopicName): LogTopic?
}

interface LogBrokerRepository :
    CreateTopic,
    AppendToTopic,
    ConsumeFromTopic,
    FindTopic,
    ReadFromTopic,
    Closeable {

    fun listTopics(block: TopicQuery.() -> Unit): List<LogTopic>
    fun list(topicIds: List<TopicId>) = topicIds.map(::getTopic) //FIXME as one request  ?!

    @OptIn(ExperimentalSerializationApi::class)
    fun listEvents(topic: LogTopic, block: EventQuery.() -> Unit): List<EventWithId> {
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