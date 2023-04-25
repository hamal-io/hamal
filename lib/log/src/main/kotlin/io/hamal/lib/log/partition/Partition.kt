package io.hamal.lib.log.partition

import io.hamal.lib.log.segment.*
import io.hamal.lib.log.topic.Topic
import io.hamal.lib.util.Files
import java.nio.file.Path
import kotlin.io.path.Path

data class Partition(
    val id: Id,
    val topicId: Topic.Id,
    val path: Path
) {
    @JvmInline
    value class Id(val value: ULong) {
        constructor(value: Int) : this(value.toULong())
    }
}


// FIXME just a pass through for now - replace with proper implementation,
// like supporting multiple segments, roll over etc

class PartitionRepository private constructor(
    internal val partition: Partition,
    internal var activeSegment: Segment,
    internal var activeSegmentRepository: SegmentRepository
) : ChunkAppender, ChunkReader, ChunkCounter, AutoCloseable {

    companion object {
        fun open(partition: Partition): PartitionRepository {
            val path = ensureDirectoryExists(partition)
            val segment = Segment(
                Segment.Id(0),
                path = path,
                partitionId = partition.id,
                topicId = partition.topicId
            )

            return PartitionRepository(
                partition = partition,
                activeSegment = segment,
                activeSegmentRepository = SegmentRepository.open(segment)
            )
        }

        private fun ensureDirectoryExists(partition: Partition): Path {
            val result = partition.path.resolve(Path(String.format("partition-%04d", partition.id.value.toLong())))
            Files.createDirectories(result)
            return result
        }
    }

    override fun append(vararg bytes: ByteArray): List<Chunk.Id> {
        return activeSegmentRepository.append(*bytes)
    }

    override fun read(firstId: Chunk.Id, limit: Int): List<Chunk> {
        return activeSegmentRepository.read(firstId, limit)
    }

    override fun count() = activeSegmentRepository.count()

    override fun close() {
        activeSegmentRepository.close()
    }
}

internal fun PartitionRepository.clear() {
    activeSegmentRepository.clear()
}