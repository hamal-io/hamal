package io.hamal.repository.api.log

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.DomainId
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.TopicId
import java.time.Instant

data class ChunkId(override val value: SnowflakeId) : DomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
}

class Chunk(
    val id: ChunkId,
    val segmentId: Segment.Id,
    val topicId: TopicId,
    val bytes: ByteArray,
    val instant: Instant
)


interface ChunkAppender {
    fun append(cmdId: CmdId, bytes: ByteArray)
}

interface ChunkReader {
    fun read(firstId: ChunkId, limit: Int = 1): List<Chunk>
}

interface ChunkCounter {
    fun count(): ULong
}
