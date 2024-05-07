package io.hamal.repository.api.log

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.ValueSnowflakeId
import io.hamal.lib.common.value.ValueVariableSnowflakeId
import io.hamal.lib.domain.vo.LogTopicId
import java.time.Instant

class LogEventId(override val value: ValueSnowflakeId) : ValueVariableSnowflakeId() {
    companion object {
        fun LogEventId(value: SnowflakeId) = LogEventId(ValueSnowflakeId(value))
        fun LogEventId(value: Int) = LogEventId(ValueSnowflakeId(SnowflakeId(value.toLong())))
        fun LogEventId(value: String) = LogEventId(ValueSnowflakeId(SnowflakeId(value.toLong(16))))
    }
}

data class LogEvent(
    val id: LogEventId,
    val topicId: LogTopicId,
    val segmentId: LogSegmentId,
    val bytes: ByteArray,
    val instant: Instant
)