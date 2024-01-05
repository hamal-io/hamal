package io.hamal.repository.api.log

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.Serde
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.CmdRepository
import io.hamal.repository.api.log.BrokerTopicsRepository.TopicQuery

interface CreateTopic {
    fun create(cmdId: CmdId, topicToCreate: TopicToCreate): Topic

    data class TopicToCreate(
        val id: TopicId,
        val name: TopicName,
        val flowId: FlowId,
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

    fun findTopic(flowId: FlowId, topicName: TopicName): Topic?
}

interface ResolveTopic {
    fun resolveTopic(flowId: FlowId, name: TopicName): Topic?
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

    fun listEntries(topic: Topic, query: TopicEntryQuery): List<TopicEntry> {
        val firstId = ChunkId(query.afterId.value.value.toInt() + 1)
        return read(firstId, topic, query.limit.value)
            .map { chunk ->
                val payload = Serde.decompressAndDeserialize(TopicEntryPayload::class, chunk.bytes)
                TopicEntry(
                    id = TopicEntryId(chunk.id.value.toInt()),
                    payload = payload
                )
            }
    }

    data class TopicEntryQuery(
        var afterId: TopicEntryId = TopicEntryId(0),
        var limit: Limit = Limit(1)
    )
}