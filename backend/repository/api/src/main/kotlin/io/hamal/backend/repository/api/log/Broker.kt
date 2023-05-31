package io.hamal.backend.repository.api.log

import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import java.io.Closeable
import java.nio.file.Path

data class LogBroker(
    val id: Id,
    val path: Path
) {
    @JvmInline
    value class Id(val value: ULong) {
        constructor(value: Int) : this(value.toULong())
    }
}

interface AppendToTopic {
    fun append(reqId: ReqId, topic: LogTopic, bytes: ByteArray)
}

interface ConsumeFromTopic {
    fun consume(groupId: GroupId, topic: LogTopic, limit: Int): List<LogChunk>

    fun commit(groupId: GroupId, topic: LogTopic, chunkId: LogChunkId)
}

interface ReadFromTopic {
    fun read(lastId: LogChunkId, topic: LogTopic, limit: Int): List<LogChunk>
}

interface ResolveTopic {
    fun resolveTopic(topicName: TopicName): LogTopic
}

interface GetTopics {
    fun topics(): Set<LogTopic>
}

interface GetTopic {
    fun get(topicId: TopicId): LogTopic = requireNotNull(find(topicId)) { "Topic with id $topicId not found" }

    fun find(topicId: TopicId): LogTopic?
}

interface LogBrokerRepository : AppendToTopic, ConsumeFromTopic, GetTopic, GetTopics, ReadFromTopic, ResolveTopic,
    Closeable