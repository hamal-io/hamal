package io.hamal.backend.repository.memory.log

import io.hamal.backend.repository.api.log.LogChunk
import io.hamal.backend.repository.api.log.LogChunkId
import io.hamal.backend.repository.api.log.LogSegment
import io.hamal.backend.repository.api.log.LogSegmentRepository
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.CmdId
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class MemoryLogSegmentRepository(
    private val segment: LogSegment
) : LogSegmentRepository {

    private val store = mutableListOf<LogChunk>()
    private val lock = ReentrantLock()

    override fun append(cmdId: CmdId, bytes: ByteArray) {
        lock.withLock {
            store.add(
                LogChunk(
                    id = LogChunkId(store.size + 1),
                    segmentId = segment.id,
                    shard = segment.shard,
                    topicId = segment.topicId,
                    bytes = bytes,
                    instant = TimeUtils.now()
                )
            )
        }
    }

    override fun read(firstId: LogChunkId, limit: Int): List<LogChunk> {
        if (limit < 1) {
            return listOf()
        }
        return lock.withLock {
            store.drop(firstId.value.value.toInt() - 1)
                .take(limit)
        }
    }

    override fun count(): ULong {
        return lock.withLock { store.size.toULong() }
    }

    override fun close() { }

    override fun clear() {
        lock.withLock { store.clear() }
    }
}
