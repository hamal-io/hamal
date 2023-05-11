package io.hamal.lib.domain.vo

import io.hamal.lib.domain.util.SnowflakeId
import io.hamal.lib.domain.vo.base.DomainId
import io.hamal.lib.domain.vo.base.DomainIdSerializer
import io.hamal.lib.domain.vo.base.Reference
import io.hamal.lib.domain.vo.base.ReferenceSerializer
import kotlinx.serialization.Serializable


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

