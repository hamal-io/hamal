package io.hamal.lib.log.segment

import io.hamal.lib.log.partition.Partition
import io.hamal.lib.log.topic.Topic
import java.time.Instant

data class Chunk(
    val id: Id,
    val segmentId: Segment.Id,
    val partitionId: Partition.Id,
    val topicId: Topic.Id,
    val bytes: ByteArray,
    val instant: Instant
) {

    @JvmInline
    value class Id(val value: ULong) {
        constructor(value: Int) : this(value.toULong())
    }
}


interface ChunkAppender {
    fun append(vararg bytes: ByteArray): List<Chunk.Id>
}

interface ChunkReader {
    fun read(firstId: Chunk.Id, limit: Int = 1): List<Chunk>
}

interface ChunkCounter {
    fun count(): ULong
}
