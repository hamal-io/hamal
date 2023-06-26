package io.hamal.lib.domain.vo

import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.vo.base.DomainAt
import io.hamal.lib.domain.vo.base.DomainAtSerializer
import kotlinx.serialization.Serializable
import java.time.Instant


@Serializable(with = InvokedAt.Serializer::class)
class InvokedAt(override val value: Instant) : DomainAt() {
    companion object {
        @JvmStatic
        fun now(): InvokedAt = InvokedAt(TimeUtils.now())
    }

    internal object Serializer : DomainAtSerializer<InvokedAt>(::InvokedAt)
}

@Serializable(with = ScheduledAt.Serializer::class)
class ScheduledAt(override val value: Instant) : DomainAt() {
    companion object {
        @JvmStatic
        fun now(): ScheduledAt = ScheduledAt(TimeUtils.now())
    }

    internal object Serializer : DomainAtSerializer<ScheduledAt>(::ScheduledAt)
}

@Serializable(with = QueuedAt.Serializer::class)
class QueuedAt(override val value: Instant) : DomainAt() {
    companion object {
        @JvmStatic
        fun now(): QueuedAt = QueuedAt(TimeUtils.now())
    }

    internal object Serializer : DomainAtSerializer<QueuedAt>(::QueuedAt)
}

@Serializable(with = CompletedAt.Serializer::class)
class CompletedAt(override val value: Instant) : DomainAt() {
    companion object {
        @JvmStatic
        fun now(): CompletedAt = CompletedAt(TimeUtils.now())
    }

    internal object Serializer : DomainAtSerializer<CompletedAt>(::CompletedAt)
}

@Serializable(with = FailedAt.Serializer::class)
class FailedAt(override val value: Instant) : DomainAt() {
    companion object {
        @JvmStatic
        fun now(): FailedAt = FailedAt(TimeUtils.now())
    }

    internal object Serializer : DomainAtSerializer<FailedAt>(::FailedAt)
}