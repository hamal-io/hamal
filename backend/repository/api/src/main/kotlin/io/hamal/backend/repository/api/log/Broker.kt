package io.hamal.backend.repository.api.log

import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
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

interface GetTopics<TOPIC : LogTopic> {
    fun queryTopics(): List<TOPIC>
}

interface FindTopic<TOPIC : LogTopic> {
    fun get(topicId: TopicId): TOPIC = requireNotNull(find(topicId)) { "Topic with id $topicId not found" }

    fun find(topicId: TopicId): TOPIC?

    fun find(topicName: TopicName): TOPIC?
}

interface LogBrokerRepository<TOPIC : LogTopic> :
    CreateTopic<TOPIC>,
    AppendToTopic<TOPIC>,
    ConsumeFromTopic<TOPIC>,
    FindTopic<TOPIC>,
    GetTopics<TOPIC>,
    ReadFromTopic<TOPIC>,
    Closeable {
    fun clear()
}