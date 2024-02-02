package io.hamal.repository.memory.new_log

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.repository.api.new_log.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

data class LogSegmentMemory(
    override val id: LogSegmentId,
    override val topicId: LogTopicId,
) : LogSegment


class LogSegmentMemoryRepository(
    internal val segment: LogSegmentMemory
) : LogSegmentRepository {

    override fun append(cmdId: CmdId, bytes: ByteArray): LogEntryId {
        return lock.withLock {
            LogEntryId(store.size + 1).also { id ->
                store.add(
                    LogEntry(
                        id = id,
                        segmentId = segment.id,
                        topicId = segment.topicId,
                        bytes = bytes,
                        instant = TimeUtils.now()
                    )
                )
            }
        }
    }

    override fun read(firstId: LogEntryId, limit: Limit): List<LogEntry> {
        if (limit.value < 1) {
            return listOf()
        }
        return lock.withLock {
            if (firstId.value == SnowflakeId(0)) {
                store.take(limit.value)
            } else {
                store.drop(firstId.value.toInt() - 1).take(limit.value)
            }
        }
    }

    override fun count(): ULong {
        return lock.withLock { store.size.toULong() }
    }

    override fun clear() {
        lock.withLock { store.clear() }
    }

    override fun close() {}

    private val store = mutableListOf<LogEntry>()
    private val lock = ReentrantLock()
}