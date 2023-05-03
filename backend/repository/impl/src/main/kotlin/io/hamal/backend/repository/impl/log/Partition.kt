package io.hamal.backend.repository.impl.log

import io.hamal.backend.repository.api.log.*
import io.hamal.lib.util.Files
import java.nio.file.Path
import kotlin.io.path.Path


// FIXME just a pass through for now - replace with proper implementation,
// like supporting multiple segments, roll over etc

class PartitionRepository private constructor(
    internal val partition: Partition,
    internal var activeSegment: Segment,
    internal var activeSegmentRepository: SegmentRepository
) : ChunkAppender, ChunkReader, ChunkCounter, AutoCloseable {

    companion object {
        fun open(partition: Partition): PartitionRepository {
            TODO()
//            val path = ensureDirectoryExists(partition)
//            val segment = Segment(
//                Segment.Id(0),
//                path = path,
//                partitionId = partition.id,
//                topicId = partition.topicId,
//                shard = partition.shard
//            )
//
//            return PartitionRepository(
//                partition = partition,
//                activeSegment = segment,
//                activeSegmentRepository = SegmentRepository.open(segment)
//            )
        }

        private fun ensureDirectoryExists(partition: Partition): Path {
            val result = partition.path.resolve(Path(String.format("partition-%04d", partition.id.value.toLong())))
            Files.createDirectories(result)
            return result
        }
    }

    override fun append(vararg bytes: ByteArray): List<Chunk.Id> {
        TODO()
//        return activeSegmentRepository.append(*bytes)
    }

    override fun read(firstId: Chunk.Id, limit: Int): List<Chunk> {
        TODO()
//        return activeSegmentRepository.read(firstId, limit)
    }

    override fun count(): ULong {
        TODO()
//        activeSegmentRepository.count()
    }

    override fun close() {
        TODO()
//        activeSegmentRepository.close()
    }
}

internal fun PartitionRepository.clear() {
    TODO()
//    activeSegmentRepository.clear()
}