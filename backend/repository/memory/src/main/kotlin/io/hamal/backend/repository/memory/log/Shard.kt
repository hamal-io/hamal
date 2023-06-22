package io.hamal.backend.repository.memory.log

import io.hamal.backend.repository.api.log.*
import io.hamal.lib.common.Shard
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.vo.TopicId

data class MemoryLogShard(
    override val id: Shard,
    override val topicId: TopicId
) : LogShard


class MemoryLogShardRepository(
    logShard: LogShard
) : LogShardRepository {

    private var activeSegment: MemoryLogSegment
    private var activeLogSegmentRepository: MemoryLogSegmentRepository

    init {
        activeSegment = MemoryLogSegment(
            LogSegment.Id(0),
            shard = logShard.id,
            topicId = logShard.topicId
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

    override fun close() {
        activeLogSegmentRepository.close()
    }

    override fun clear() {
        activeLogSegmentRepository.clear()
    }

}