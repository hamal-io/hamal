package io.hamal.lib.vo

import io.hamal.lib.util.SnowflakeId
import io.hamal.lib.util.TimeUtils
import io.hamal.lib.vo.base.*
import kotlinx.serialization.Serializable
import java.time.Instant


@Serializable(with = TriggerId.Serializer::class)
class TriggerId(override val value: SnowflakeId) : DomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong()))

    internal object Serializer : DomainIdSerializer<TriggerId>(::TriggerId)
}

@Serializable(with = TriggerReference.Serializer::class)
class TriggerReference(override val value: Value) : Reference() {
    constructor(value: String) : this(Value(value))

    internal object Serializer : ReferenceSerializer<TriggerReference>(::TriggerReference)
}

@Serializable(with = InvokedTriggerId.Serializer::class)
class InvokedTriggerId(override val value: SnowflakeId) : DomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))

    internal object Serializer : DomainIdSerializer<InvokedTriggerId>(::InvokedTriggerId)
}

@Serializable(with = InvokedAt.Serializer::class)
class InvokedAt(override val value: Instant) : DomainAt() {
    companion object {
        @JvmStatic
        fun now(): InvokedAt = InvokedAt(TimeUtils.now())
    }

    internal object Serializer : DomainAtSerializer<InvokedAt>(::InvokedAt)
}