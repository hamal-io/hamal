package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.UpdatedAt
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.vo.base.DomainAt
import java.time.Instant


class InvokedAt(override val value: Instant) : DomainAt() {
    companion object {
        @JvmStatic
        fun now(): InvokedAt = InvokedAt(TimeUtils.now())
    }
}

class ScheduledAt(override val value: Instant) : DomainAt() {
    companion object {
        @JvmStatic
        fun now(): ScheduledAt = ScheduledAt(TimeUtils.now())
    }
}

class QueuedAt(override val value: Instant) : DomainAt() {
    companion object {
        @JvmStatic
        fun now(): QueuedAt = QueuedAt(TimeUtils.now())
    }
}

class CompletedAt(override val value: Instant) : DomainAt() {
    companion object {
        @JvmStatic
        fun now(): CompletedAt = CompletedAt(TimeUtils.now())
    }
}

class FailedAt(override val value: Instant) : DomainAt() {
    companion object {
        @JvmStatic
        fun now(): FailedAt = FailedAt(TimeUtils.now())
    }
}

class RecordedAt(override val value: Instant) : DomainAt() {
    companion object {
        @JvmStatic
        fun now(): RecordedAt = RecordedAt(TimeUtils.now())
    }

    fun toUpdatedAt() = UpdatedAt(value)
}

