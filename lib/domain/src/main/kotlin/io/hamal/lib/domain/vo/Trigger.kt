package io.hamal.lib.domain.vo

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.vo.base.DomainId
import io.hamal.lib.domain.vo.base.DomainIdSerializer
import io.hamal.lib.domain.vo.base.Ref
import io.hamal.lib.domain.vo.base.RefSerializer
import kotlinx.serialization.Serializable


@Serializable(with = TriggerId.Serializer::class)
class TriggerId(override val value: SnowflakeId) : DomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong()))

    internal object Serializer : DomainIdSerializer<TriggerId>(::TriggerId)
}

@Serializable(with = TriggerRef.Serializer::class)
class TriggerRef(override val value: Value) : Ref() {
    constructor(value: String) : this(Value(value))

    internal object Serializer : RefSerializer<TriggerRef>(::TriggerRef)
}

@Serializable(with = CauseId.Serializer::class)
class CauseId(override val value: SnowflakeId) : DomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))

    internal object Serializer : DomainIdSerializer<CauseId>(::CauseId)
}

