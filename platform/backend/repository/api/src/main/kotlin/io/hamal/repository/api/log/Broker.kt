package io.hamal.repository.api.log

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.CmdRepository
import io.hamal.repository.api.log.BrokerTopicsRepository.TopicQuery
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf

interface CreateTopic {
    fun create(cmdId: CmdId, topicToCreate: TopicToCreate): Topic

    data class TopicToCreate(
        val id: TopicId,
        val name: TopicName,
        val namespaceId: NamespaceId,
        val groupId: GroupId
    )
}

interface AppendToTopic {
    fun append(cmdId: CmdId, topic: Topic, bytes: ByteArray)
}

interface ConsumeFromTopic {
    fun consume(consumerId: ConsumerId, topic: Topic, limit: Int): List<Chunk>

    fun commit(consumerId: ConsumerId, topic: Topic, chunkId: ChunkId)
}

interface ReadFromTopic {
    fun read(firstId: ChunkId, topic: Topic, limit: Int): List<Chunk>
}

interface FindTopic {
    fun getTopic(topicId: TopicId): Topic = findTopic(topicId) ?: throw NoSuchElementException("Topic not found")

    fun findTopic(topicId: TopicId): Topic?

    fun findTopic(namespaceId: NamespaceId, topicName: TopicName): Topic?
}

interface ResolveTopic {
    fun resolveTopic(namespaceId: NamespaceId, name: TopicName): Topic?
}

interface BrokerRepository :
    CmdRepository,
    CreateTopic,
    AppendToTopic,
    ConsumeFromTopic,
    FindTopic,
    ReadFromTopic,
    ResolveTopic {

    fun listTopics(query: TopicQuery): List<Topic>
    fun list(topicIds: List<TopicId>) = topicIds.map(::getTopic) //FIXME as one request  ?!

    @OptIn(ExperimentalSerializationApi::class)
    fun listEntries(topic: Topic, query: TopicEntryQuery): List<TopicEntry> {
        val firstId = ChunkId(SnowflakeId(query.afterId.value.value + 1))
        return read(firstId, topic, query.limit.value)
            .map { chunk ->
                val payload = ProtoBuf.decodeFromByteArray(TopicEntryPayload.serializer(), chunk.bytes)
                TopicEntry(
                    id = TopicEntryId(chunk.id.value),
                    payload = payload
                )
            }
    }

    data class TopicEntryQuery(
        var afterId: TopicEntryId = TopicEntryId(0),
        var limit: Limit = Limit(1)
    )
}