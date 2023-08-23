package io.hamal.lib.domain.vo

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.common.domain.DomainId
import io.hamal.lib.common.domain.DomainIdSerializer
import io.hamal.lib.common.domain.DomainName
import io.hamal.lib.common.domain.DomainNameSerializer
import io.hamal.lib.domain.vo.base.Inputs
import io.hamal.lib.domain.vo.base.InputsSerializer
import io.hamal.lib.kua.type.DepTableType
import kotlinx.serialization.Serializable

@Serializable(with = FuncId.Serializer::class)
class FuncId(override val value: SnowflakeId) : DomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))

    internal object Serializer : DomainIdSerializer<FuncId>(::FuncId)
}


@Serializable(with = FuncName.Serializer::class)
class FuncName(override val value: String) : DomainName() {
    internal object Serializer : DomainNameSerializer<FuncName>(::FuncName)
}

@Serializable(with = FuncInputs.Serializer::class)
class FuncInputs(override val value: DepTableType = DepTableType()) : Inputs() {
    internal object Serializer : InputsSerializer<FuncInputs>(::FuncInputs)
}
