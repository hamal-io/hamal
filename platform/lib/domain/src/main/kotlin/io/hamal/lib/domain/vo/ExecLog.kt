package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.vo.base.DomainAt
import java.time.Instant


class ExecLogId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))
}

data class ExecLogMessage(val value: String)

class ExecLogTimestamp(override val value: Instant) : DomainAt() {
    companion object {
        @JvmStatic
        fun now(): ExecLogTimestamp = ExecLogTimestamp(TimeUtils.now())
    }
}
