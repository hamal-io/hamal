package io.hamal.repository.api.log

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.CmdRepository
import io.hamal.repository.api.log.BrokerTopicsRepository.TopicQuery

interface CreateTopic {
    fun create(cmdId: CmdId, topicToCreate: TopicToCreate): DepTopic

    data class TopicToCreate(
        val id: TopicId,
        val name: TopicName,
        val flowId: FlowId,
        val groupId: GroupId
    )
}

interface AppendToTopic {
    fun append(cmdId: CmdId, topic: DepTopic, bytes: ByteArray)
}

interface ConsumeFromTopic {
    fun consume(consumerId: ConsumerId, topic: DepTopic, limit: Int): List<Chunk>

    fun commit(consumerId: ConsumerId, topic: DepTopic, chunkId: ChunkId)
}

interface ReadFromTopic {
    fun read(firstId: ChunkId, topic: DepTopic, limit: Int): List<Chunk>
}

interface FindTopic {
    fun getTopic(topicId: TopicId): DepTopic = findTopic(topicId) ?: throw NoSuchElementException("Topic not found")

    fun findTopic(topicId: TopicId): DepTopic?

    fun findTopic(flowId: FlowId, topicName: TopicName): DepTopic?
}

interface ResolveTopic {
    fun resolveTopic(flowId: FlowId, name: TopicName): DepTopic?
}

@Deprecated("")
interface BrokerRepository :
    CmdRepository,
    CreateTopic,
    AppendToTopic,
    ConsumeFromTopic,
    FindTopic,
    ReadFromTopic,
    ResolveTopic {

    fun listTopics(query: TopicQuery): List<DepTopic>
    fun list(topicIds: List<TopicId>) = topicIds.map(::getTopic) //FIXME as one request  ?!

    fun listEntries(topic: DepTopic, query: TopicEntryQuery): List<TopicEntry> {
        val firstId = ChunkId(query.afterId.value.value.toInt() + 1)
        return read(firstId, topic, query.limit.value)
            .map { chunk ->
                val payload = json.decompressAndDeserialize(TopicEntryPayload::class, chunk.bytes)
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