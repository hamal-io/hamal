package io.hamal.lib.domain.vo

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.vo.base.DomainId
import io.hamal.lib.domain.vo.base.DomainIdSerializer
import io.hamal.lib.domain.vo.base.Ref
import io.hamal.lib.domain.vo.base.RefSerializer
import kotlinx.serialization.Serializable

@Serializable(with = FuncId.Serializer::class)
class FuncId(override val value: SnowflakeId) : DomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))

    internal object Serializer : DomainIdSerializer<FuncId>(::FuncId)
}


@Serializable(with = FuncRef.Serializer::class)
class FuncRef(override val value: Value) : Ref() {
    constructor(value: String) : this(Value(value))

    internal object Serializer : RefSerializer<FuncRef>(::FuncRef)
}
