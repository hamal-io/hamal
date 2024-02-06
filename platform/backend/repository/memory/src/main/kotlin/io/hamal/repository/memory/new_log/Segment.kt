package io.hamal.repository.memory.new_log

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.vo.LogTopicId
import io.hamal.repository.api.log.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

data class LogSegmentMemory(
    override val id: LogSegmentId,
    override val topicId: LogTopicId,
) : LogSegment


class LogSegmentMemoryRepository(
    private val segment: LogSegmentMemory
) : LogSegmentRepository {

    override fun append(cmdId: CmdId, bytes: ByteArray) {
        return lock.withLock {
            store.add(
                LogEvent(
                    id = LogEventId(store.size + 1),
                    segmentId = segment.id,
                    topicId = segment.topicId,
                    bytes = bytes,
                    instant = TimeUtils.now()
                )
            )
        }
    }

    override fun read(firstId: LogEventId, limit: Limit): List<LogEvent> {
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

    override fun count(): Count {
        return lock.withLock { Count(store.size) }
    }

    override fun clear() {
        lock.withLock { store.clear() }
    }

    override fun close() {}

    private val store = mutableListOf<LogEvent>()
    private val lock = ReentrantLock()
}