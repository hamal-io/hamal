package io.hamal.backend.repository.api.log

import io.hamal.lib.core.Shard
import java.io.Closeable
import java.nio.file.Path

data class Segment(
    val id: Id,
    val partitionId: Partition.Id,
    val topicId: Topic.Id,
    val path: Path,
    val shard: Shard
) {
    @JvmInline
    value class Id(val value: ULong) {
        constructor(value: Int) : this(value.toULong())
    }
}


interface SegmentRepository : ChunkAppender, ChunkReader, ChunkCounter, Closeable {
    fun clear()
}