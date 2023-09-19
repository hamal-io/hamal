package io.hamal.repository.memory.log

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.vo.TopicId
import io.hamal.repository.api.log.Chunk
import io.hamal.repository.api.log.ChunkId
import io.hamal.repository.api.log.Segment
import io.hamal.repository.api.log.SegmentRepository
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

data class MemorySegment(
    override val id: Segment.Id,
    override val topicId: TopicId,
) : Segment


class MemorySegmentRepository(
    private val segment: MemorySegment
) : SegmentRepository {

    private val store = mutableListOf<Chunk>()
    private val lock = ReentrantLock()

    override fun append(cmdId: CmdId, bytes: ByteArray) {
        lock.withLock {
            store.add(
                Chunk(
                    id = ChunkId(store.size + 1),
                    segmentId = segment.id,
                    topicId = segment.topicId,
                    bytes = bytes,
                    instant = TimeUtils.now()
                )
            )
        }
    }

    override fun read(firstId: ChunkId, limit: Int): List<Chunk> {
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
