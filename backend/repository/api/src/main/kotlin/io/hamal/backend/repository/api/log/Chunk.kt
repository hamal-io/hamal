package io.hamal.backend.repository.api.log

import io.hamal.lib.common.Shard
import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.base.DomainId
import java.time.Instant

data class LogChunkId(override val value: SnowflakeId) : DomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
}

data class LogChunk(
    val id: LogChunkId,
    val segmentId: LogSegment.Id,
    val shard: Shard,
    val topicId: TopicId,
    val bytes: ByteArray,
    val instant: Instant
) {
}


interface LogChunkAppender {
    fun append(bytes: ByteArray): LogChunkId
}

interface LogChunkReader {
    fun read(firstId: LogChunkId, limit: Int = 1): List<LogChunk>
}

interface LogChunkCounter {
    fun count(): ULong
}
