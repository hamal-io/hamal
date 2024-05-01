package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.domain.ValueObjectInstant
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.common.value.ValueVariableString
import java.time.Instant


class ExecLogId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
}

class ExecLogMessage(override val value: ValueString) : ValueVariableString() {
    companion object {
        fun ExecLogMessage(value: String) = ExecLogMessage(ValueString(value))
    }
}

class ExecLogTimestamp(override val value: Instant) : ValueObjectInstant() {
    companion object {
        @JvmStatic
        fun now(): ExecLogTimestamp = ExecLogTimestamp(TimeUtils.now())
    }
}
