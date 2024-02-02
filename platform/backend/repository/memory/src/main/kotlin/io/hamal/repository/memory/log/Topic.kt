package io.hamal.repository.memory.log

import io.hamal.lib.common.domain.CmdId
import io.hamal.repository.api.log.*


// FIXME just a pass through for now - replace with proper implementation,
// like supporting multiple partitions, partitioning by key
// keeping track of consumer group ids
class TopicMemoryRepository(
    internal val topic: DepTopic
) : DepTopicRepository {

    private var activeSegment: SegmentMemory
    private var activeSegmentRepository: SegmentMemoryRepository

    init {
        activeSegment = SegmentMemory(
            id = Segment.Id(0),
            topicId = topic.id
        )
        activeSegmentRepository = SegmentMemoryRepository(activeSegment)
    }

    override fun append(cmdId: CmdId, bytes: ByteArray) {
        activeSegmentRepository.append(cmdId, bytes)
    }

    override fun read(firstId: ChunkId, limit: Int): List<Chunk> {
        return activeSegmentRepository.read(firstId, limit)
    }

    override fun count(): ULong {
        return activeSegmentRepository.count()
    }

    override fun clear() {
        activeSegmentRepository.clear()
    }

    override fun close() {
        activeSegmentRepository.close()
    }
}
