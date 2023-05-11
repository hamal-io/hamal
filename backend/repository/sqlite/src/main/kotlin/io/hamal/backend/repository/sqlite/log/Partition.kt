package io.hamal.backend.repository.sqlite.log

import io.hamal.backend.repository.api.log.*
import io.hamal.backend.repository.sqlite.BaseRepository
import io.hamal.backend.repository.sqlite.internal.Connection
import io.hamal.lib.domain.Shard
import java.nio.file.Path


// FIXME just a pass through for now - replace with proper implementation,
// like supporting multiple segments, roll over etc

class DefaultPartitionRepository(
    internal val partition: Partition
) : BaseRepository(object : Config {
    override val path: Path get() = partition.path
    override val filename: String get() = String.format("partition-%04d", partition.id.value.toLong())
    override val shard: Shard get() = partition.shard

}), PartitionRepository {

    internal var activeSegment: Segment
    internal var activeSegmentRepository: SegmentRepository

    init {
        activeSegment = Segment(
            Segment.Id(0),
            path = partition.path.resolve(config.filename),
            partitionId = partition.id,
            topicId = partition.topicId,
            shard = partition.shard
        )
        activeSegmentRepository = DefaultSegmentRepository(activeSegment)
    }

    override fun append(bytes: ByteArray): Chunk.Id {
        return activeSegmentRepository.append(bytes)
    }

    override fun read(firstId: Chunk.Id, limit: Int): List<Chunk> {
        return activeSegmentRepository.read(firstId, limit)
    }

    override fun count(): ULong {
        return activeSegmentRepository.count()
    }

    override fun setupConnection(connection: Connection) {}

    override fun setupSchema(connection: Connection) {}

    override fun clear() {
        activeSegmentRepository.clear()
    }

    override fun close() {
        activeSegmentRepository.close()
    }
}