package io.hamal.repository.memory.log

import io.hamal.repository.api.log.*
import io.hamal.lib.common.domain.CmdId


// FIXME just a pass through for now - replace with proper implementation,
// like supporting multiple partitions, partitioning by key
// keeping track of consumer group ids
class MemoryLogTopicRepository(
    internal val topic: LogTopic
) : LogTopicRepository {

    private var activeSegment: MemoryLogSegment
    private var activeLogSegmentRepository: MemoryLogSegmentRepository

    init {
        activeSegment = MemoryLogSegment(
            id = LogSegment.Id(0),
            topicId = topic.id
        )
        activeLogSegmentRepository = MemoryLogSegmentRepository(activeSegment)
    }

    override fun append(cmdId: CmdId, bytes: ByteArray) {
        activeLogSegmentRepository.append(cmdId, bytes)
    }

    override fun read(firstId: LogChunkId, limit: Int): List<LogChunk> {
        return activeLogSegmentRepository.read(firstId, limit)
    }

    override fun count(): ULong {
        return activeLogSegmentRepository.count()
    }

    override fun clear() {
        activeLogSegmentRepository.clear()
    }

    override fun close() {
        activeLogSegmentRepository.close()
    }
}
