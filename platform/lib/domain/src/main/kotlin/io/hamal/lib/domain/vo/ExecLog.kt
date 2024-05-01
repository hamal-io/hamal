package io.hamal.lib.domain.vo

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.common.value.*
import java.time.Instant


class ExecLogId(override val value: ValueSnowflakeId) : ValueVariableSnowflakeId() {
    companion object {
        fun ExecLogId(value: SnowflakeId) = ExecLogId(ValueSnowflakeId(value))
        fun ExecLogId(value: Int) = ExecLogId(ValueSnowflakeId(SnowflakeId(value.toLong())))
        fun ExecLogId(value: String) = ExecLogId(ValueSnowflakeId(SnowflakeId(value.toLong(16))))
    }
}

class ExecLogMessage(override val value: ValueString) : ValueVariableString() {
    companion object {
        fun ExecLogMessage(value: String) = ExecLogMessage(ValueString(value))
    }
}

class ExecLogTimestamp(override val value: ValueInstant) : ValueVariableInstant() {
    companion object {
        @JvmStatic
        fun now(): ExecLogTimestamp = ExecLogTimestamp(TimeUtils.now())
        fun ExecLogTimestamp(value: Instant) = ExecLogTimestamp(ValueInstant(value))
    }
}
