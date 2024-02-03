package io.hamal.repository.api.new_log

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.LogTopicId
import io.hamal.repository.api.CmdRepository


interface LogTopic {
    val id: LogTopicId
}

interface LogTopicRepository : CmdRepository {
    fun append(cmdId: CmdId, bytes: ByteArray): LogEntryId

    fun read(firstId: LogEntryId, limit: Limit = Limit(1)): List<LogEntry>

    fun countEntries(): ULong
}
