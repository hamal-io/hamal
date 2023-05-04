package io.hamal.backend.repository.api.log

import io.hamal.lib.Shard
import io.hamal.lib.util.SnowflakeId
import io.hamal.lib.vo.base.DomainId
import java.time.Instant

data class Chunk(
    val id: Id,
    val segmentId: Segment.Id,
    val partitionId: Partition.Id,
    val topicId: Topic.Id,
    val shard: Shard,
    val bytes: ByteArray,
    val instant: Instant
) {
    data class Id(override val value: SnowflakeId) : DomainId() {
        constructor(value: Int) : this(SnowflakeId(value.toLong()))
    }
}


interface ChunkAppender {
    fun append(bytes: ByteArray): Chunk.Id
}

interface ChunkReader {
    fun read(firstId: Chunk.Id, limit: Int = 1): List<Chunk>
}

interface ChunkCounter {
    fun count(): ULong
}
