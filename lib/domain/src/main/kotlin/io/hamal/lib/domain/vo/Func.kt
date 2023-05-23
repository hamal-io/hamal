package io.hamal.lib.domain.vo

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.vo.base.DomainId
import io.hamal.lib.domain.vo.base.DomainIdSerializer
import io.hamal.lib.domain.vo.base.Name
import io.hamal.lib.domain.vo.base.NameSerializer
import kotlinx.serialization.Serializable

@Serializable(with = FuncId.Serializer::class)
class FuncId(override val value: SnowflakeId) : DomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))

    internal object Serializer : DomainIdSerializer<FuncId>(::FuncId)
}


@Serializable(with = FuncName.Serializer::class)
class FuncName(override val value: Value) : Name() {
    constructor(value: String) : this(Value(value))

    internal object Serializer : NameSerializer<FuncName>(::FuncName)
}
