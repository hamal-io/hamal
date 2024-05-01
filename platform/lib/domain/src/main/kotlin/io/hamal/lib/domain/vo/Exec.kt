package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.ValueObjectHotObject
import io.hamal.lib.common.domain.ValueObjectInstant
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.common.value.*
import io.hamal.lib.domain._enum.CodeType
import java.time.Instant

class ExecId(override val value: ValueSnowflakeId) : ValueVariableSnowflakeId() {
    companion object {
        fun ExecId(value: SnowflakeId) = ExecId(ValueSnowflakeId(value))
        fun ExecId(value: Int) = ExecId(ValueSnowflakeId(SnowflakeId(value.toLong())))
        fun ExecId(value: String) = ExecId(ValueSnowflakeId(SnowflakeId(value.toLong(16))))
    }
}

class ExecType(override val value: ValueString) : ValueVariableString() {
    companion object {
        fun ExecType(value: String) = ExecType(ValueString(value))
    }
}

class ExecInputs(override val value: HotObject = HotObject.empty) : ValueObjectHotObject()

data class ExecCode(
    val id: CodeId? = null,
    val version: CodeVersion? = null,
    val value: ValueCode? = null,
    val type: CodeType? = null
)

enum class ExecStatus(val value: Int) {
    Planned(1),
    Scheduled(2),
    Queued(3),
    Started(4),
    Completed(5),
    Failed(6);

    companion object {
        fun valueOf(value: Int) = requireNotNull(mapped[value]) { "$value is not an exec status" }

        private val mapped = ExecStatus.values()
            .associateBy { it.value }
    }
}


class ExecToken(override val value: ValueString) : ValueVariableString() {
    companion object {
        fun ExecToken(value: String) = ExecToken(ValueString(value))
    }
}

class ExecResult(override val value: HotObject = HotObject.empty) : ValueObjectHotObject()

class ExecState(override val value: HotObject = HotObject.empty) : ValueObjectHotObject()

class ExecScheduledAt(override val value: Instant) : ValueObjectInstant() {
    companion object {
        @JvmStatic
        fun now(): ExecScheduledAt = ExecScheduledAt(TimeUtils.now())
    }
}

class ExecQueuedAt(override val value: Instant) : ValueObjectInstant() {
    companion object {
        @JvmStatic
        fun now(): ExecQueuedAt = ExecQueuedAt(TimeUtils.now())
    }
}

class ExecCompletedAt(override val value: Instant) : ValueObjectInstant() {
    companion object {
        @JvmStatic
        fun now(): ExecCompletedAt = ExecCompletedAt(TimeUtils.now())
    }
}

class ExecFailedAt(override val value: Instant) : ValueObjectInstant() {
    companion object {
        @JvmStatic
        fun now(): ExecFailedAt = ExecFailedAt(TimeUtils.now())
    }
}