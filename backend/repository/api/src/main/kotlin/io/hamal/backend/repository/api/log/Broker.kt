package io.hamal.backend.repository.api.log

import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.vo.TenantId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import java.io.Closeable

interface LogBroker {
    val id: Id

    @JvmInline
    value class Id(val value: ULong) {
        constructor(value: Int) : this(value.toULong())
    }
}

interface CreateTopic<TOPIC : LogTopic> {
    fun create(cmdId: CmdId, topicToCreate: TopicToCreate)

    data class TopicToCreate(
        val id: TopicId,
        val tenantId: TenantId,
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
    fun read(lastId: LogChunkId, topic: TOPIC, limit: Int): List<LogChunk>
}

interface ResolveTopic<TOPIC : LogTopic> {
    fun resolveTopic(topicName: TopicName): TOPIC
}

interface GetTopics<TOPIC : LogTopic> {
    fun topics(): Set<TOPIC>
}

interface GetTopic<TOPIC : LogTopic> {
    fun get(topicId: TopicId): TOPIC = requireNotNull(find(topicId)) { "Topic with id $topicId not found" }

    fun find(topicId: TopicId): TOPIC?
}

interface LogBrokerRepository<TOPIC : LogTopic> :
    AppendToTopic<TOPIC>,
    ConsumeFromTopic<TOPIC>,
    GetTopic<TOPIC>,
    GetTopics<TOPIC>,
    ReadFromTopic<TOPIC>,
    ResolveTopic<TOPIC>,
    Closeable