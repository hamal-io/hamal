package io.hamal.lib.domain.vo

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

class ExecInputs(override val value: ValueObject = ValueObject.empty) : ValueVariableObject()

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

class ExecResult(override val value: ValueObject = ValueObject.empty) : ValueVariableObject()

class ExecState(override val value: ValueObject = ValueObject.empty) : ValueVariableObject()

class ExecScheduledAt(override val value: ValueInstant) : ValueVariableInstant() {
    companion object {
        @JvmStatic
        fun now(): ExecScheduledAt = ExecScheduledAt(TimeUtils.now())
        fun ExecScheduledAt(value: Instant) = ExecScheduledAt(ValueInstant(value))
    }
}

class ExecQueuedAt(override val value: ValueInstant) : ValueVariableInstant() {
    companion object {
        @JvmStatic
        fun now(): ExecQueuedAt = ExecQueuedAt(TimeUtils.now())
        fun ExecQueuedAt(value: Instant) = ExecQueuedAt(ValueInstant(value))
    }
}

class ExecCompletedAt(override val value: ValueInstant) : ValueVariableInstant() {
    companion object {
        @JvmStatic
        fun now(): ExecCompletedAt = ExecCompletedAt(TimeUtils.now())
        fun ExecCompletedAt(value: Instant) = ExecCompletedAt(ValueInstant(value))
    }
}

class ExecFailedAt(override val value: ValueInstant) : ValueVariableInstant() {
    companion object {
        @JvmStatic
        fun now(): ExecFailedAt = ExecFailedAt(TimeUtils.now())
        fun ExecFailedAt(value: Instant) = ExecFailedAt(ValueInstant(value))
    }
}