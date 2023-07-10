package io.hamal.backend.repository.memory.log

import io.hamal.backend.repository.api.log.LogChunk
import io.hamal.backend.repository.api.log.LogChunkId
import io.hamal.backend.repository.api.log.LogSegment
import io.hamal.backend.repository.api.log.LogSegmentRepository
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.TopicId
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

data class MemoryLogSegment(
    override val id: LogSegment.Id,
    override val topicId: TopicId,
) : LogSegment


class MemoryLogSegmentRepository(
    private val segment: MemoryLogSegment
) : LogSegmentRepository {

    private val store = mutableListOf<LogChunk>()
    private val lock = ReentrantLock()

    override fun append(cmdId: CmdId, bytes: ByteArray) {
        lock.withLock {
            store.add(
                LogChunk(
                    id = LogChunkId(store.size + 1),
                    segmentId = segment.id,
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
            if (firstId.value.value == 0L) {
                store.take(limit)
            } else {
                store.drop(firstId.value.value.toInt() - 1)
                    .take(limit)
            }
        }
    }

    override fun count(): ULong {
        return lock.withLock { store.size.toULong() }
    }

    override fun close() {}

    override fun clear() {
        lock.withLock { store.clear() }
    }
}
