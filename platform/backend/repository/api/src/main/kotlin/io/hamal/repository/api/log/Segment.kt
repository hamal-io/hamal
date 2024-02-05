package io.hamal.repository.api.log

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.LogTopicId
import io.hamal.repository.api.CmdRepository

class LogSegmentId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
}

interface LogSegment {
    val id: LogSegmentId
    val topicId: LogTopicId
}

interface LogSegmentRepository : CmdRepository {
    fun append(cmdId: CmdId, bytes: ByteArray): LogEntryId

    fun read(firstId: LogEntryId, limit: Limit = Limit(1)): List<LogEntry>

    fun count(): ULong
}
