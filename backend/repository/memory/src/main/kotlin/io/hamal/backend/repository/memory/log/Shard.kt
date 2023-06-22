package io.hamal.backend.repository.memory.log

import io.hamal.backend.repository.api.log.*
import io.hamal.lib.domain.CmdId


class MemoryLogShardRepository(
    logShard: LogShard
) : LogShardRepository {

    private var activeSegment: LogSegment
    private var activeLogSegmentRepository: LogSegmentRepository

    init {
        activeSegment = LogSegment(
            LogSegment.Id(0),
            shard = logShard.id,
            path = logShard.path,
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