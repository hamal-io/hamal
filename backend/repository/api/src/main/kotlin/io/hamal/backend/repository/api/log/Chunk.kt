package io.hamal.backend.repository.api.log

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.DomainId
import io.hamal.lib.domain.vo.TopicId
import java.time.Instant

data class LogChunkId(override val value: SnowflakeId) : DomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
}

class LogChunk(
    val id: LogChunkId,
    val segmentId: LogSegment.Id,
    val topicId: TopicId,
    val bytes: ByteArray,
    val instant: Instant
)


interface LogChunkAppender {
    fun append(cmdId: CmdId, bytes: ByteArray)
}

interface LogChunkReader {
    fun read(firstId: LogChunkId, limit: Int = 1): List<LogChunk>
}

interface LogChunkCounter {
    fun count(): ULong
}
