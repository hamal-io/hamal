package io.hamal.repository.api.log

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.domain.Limit.Companion.Limit
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.ValueSnowflakeId
import io.hamal.lib.common.value.ValueVariableSnowflakeId
import io.hamal.lib.domain.vo.LogTopicId
import io.hamal.repository.api.CmdRepository

class LogSegmentId(override val value: ValueSnowflakeId) : ValueVariableSnowflakeId() {
    companion object {
        fun LogSegmentId(value: SnowflakeId) = LogSegmentId(ValueSnowflakeId(value))
        fun LogSegmentId(value: Int) = LogSegmentId(ValueSnowflakeId(SnowflakeId(value.toLong())))
        fun LogSegmentId(value: String) = LogSegmentId(ValueSnowflakeId(SnowflakeId(value.toLong(16))))
    }
}

interface LogSegment {
    val id: LogSegmentId
    val topicId: LogTopicId
}

interface LogSegmentRepository : CmdRepository {
    fun append(cmdId: CmdId, bytes: ByteArray)

    fun read(firstId: LogEventId, limit: Limit = Limit(1)): List<LogEvent>

    fun count(): Count
}
