package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.ValueObjectHotObject
import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.domain.ValueObjectInstant
import io.hamal.lib.common.domain.ValueObjectString
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.util.TimeUtils
import java.time.Instant

class ExecId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))
}

class ExecType(override val value: String) : ValueObjectString()

class ExecInputs(override val value: HotObject = HotObject.empty) : ValueObjectHotObject()

data class ExecCode(
    val id: CodeId? = null,
    val version: CodeVersion? = null,
    val value: CodeValue? = null,
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


class ExecToken(override val value: String) : ValueObjectString()

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