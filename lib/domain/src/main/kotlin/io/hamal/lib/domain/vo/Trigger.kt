package io.hamal.lib.domain.vo

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.vo.base.DomainId
import io.hamal.lib.domain.vo.base.DomainIdSerializer
import io.hamal.lib.domain.vo.base.Name
import io.hamal.lib.domain.vo.base.NameSerializer
import kotlinx.serialization.Serializable


@Serializable(with = TriggerId.Serializer::class)
class TriggerId(override val value: SnowflakeId) : DomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong()))

    internal object Serializer : DomainIdSerializer<TriggerId>(::TriggerId)
}

@Serializable(with = TriggerName.Serializer::class)
class TriggerName(override val value: Value) : Name() {
    constructor(value: String) : this(Value(value))

    internal object Serializer : NameSerializer<TriggerName>(::TriggerName)
}

@Serializable(with = InvocationId.Serializer::class)
class InvocationId(override val value: SnowflakeId) : DomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))

    internal object Serializer : DomainIdSerializer<InvocationId>(::InvocationId)
}

