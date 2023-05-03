package io.hamal.backend.repository.impl.log

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
    value class Id(val value: ULong) : Comparable<Id> {
        constructor(value: Int) : this(value.toULong())

        override fun compareTo(other: Id) = value.compareTo(other.value)
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
