package io.hamal.backend.repository.memory.log

import io.hamal.backend.repository.api.log.*
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName

data class MemoryLogTopic(
    override val id: TopicId,
    override val logBrokerId: LogBroker.Id,
    override val name: TopicName,
) : LogTopic


// FIXME just a pass through for now - replace with proper implementation,
// like supporting multiple partitions, partitioning by key
// keeping track of consumer group ids
class MemoryLogTopicRepository(
    internal val topic: MemoryLogTopic
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
