package io.hamal.repository.memory.new_log

import io.hamal.lib.common.domain.CmdId
import io.hamal.repository.api.new_log.*

data class LogSegmentMemory(
    override val id: LogSegmentId,
    override val topicId: LogTopicId,
) : LogSegment


class LogSegmentMemoryRepository(
    internal val segment: LogSegmentMemory
) : LogSegmentRepository {
    
    override fun append(cmdId: CmdId, bytes: ByteArray): LogEntryId {
        TODO("Not yet implemented")
    }

    override fun read(firstId: LogEntryId, limit: Int): List<LogEntry> {
        TODO("Not yet implemented")
    }

    override fun count(): ULong {
        TODO("Not yet implemented")
    }

    override fun clear() {
        TODO("Not yet implemented")
    }

    override fun close() {
        TODO("Not yet implemented")
    }

}