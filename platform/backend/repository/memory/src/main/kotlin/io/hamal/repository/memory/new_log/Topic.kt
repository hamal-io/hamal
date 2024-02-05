package io.hamal.repository.memory.new_log

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.LogTopicId
import io.hamal.repository.api.log.*

data class LogTopicMemory(
    override val id: LogTopicId
) : LogTopic

// FIXME just a pass through for now - replace with proper implementation,
// like supporting multiple partitions, partitioning by key
// keeping track of consumer group ids
class LogTopicMemoryRepository(
    internal val topic: LogTopic
) : LogTopicRepository {

    override fun append(cmdId: CmdId, bytes: ByteArray): LogEntryId {
        return activeSegmentRepository.append(cmdId, bytes)
    }

    override fun read(firstId: LogEntryId, limit: Limit): List<LogEntry> {
        return activeSegmentRepository.read(firstId, limit)
    }

    override fun countEntries(): ULong {
        return activeSegmentRepository.count()
    }

    override fun clear() {
        return activeSegmentRepository.clear()
    }

    override fun close() {
        return activeSegmentRepository.close()
    }

    private var activeSegment = LogSegmentMemory(
        id = LogSegmentId(SnowflakeId(0)),
        topicId = topic.id
    )
    private var activeSegmentRepository = LogSegmentMemoryRepository(activeSegment)
}