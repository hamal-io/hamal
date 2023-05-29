package io.hamal.backend.repository.api.log

import io.hamal.lib.common.Shard
import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.base.DomainId
import java.time.Instant

data class LogChunk(
    val id: Id,
    val segmentId: LogSegment.Id,
    val shard: Shard,
    val topicId: TopicId,
    val bytes: ByteArray,
    val instant: Instant
) {
    data class Id(override val value: SnowflakeId) : DomainId() {
        constructor(value: Int) : this(SnowflakeId(value.toLong()))
    }
}


interface LogChunkAppender {
    fun append(bytes: ByteArray): LogChunk.Id
}

interface LogChunkReader {
    fun read(firstId: LogChunk.Id, limit: Int = 1): List<LogChunk>
}

interface LogChunkCounter {
    fun count(): ULong
}
