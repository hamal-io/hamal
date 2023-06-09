package io.hamal.lib.domain.vo

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.vo.base.*
import io.hamal.lib.script.api.value.TableValue
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
class FuncInputs(override val value: TableValue) : Inputs() {
    internal object Serializer : InputsSerializer<FuncInputs>(::FuncInputs)
}

@Serializable(with = FuncSecrets.Serializer::class)
class FuncSecrets(override val value: List<Secret>) : Secrets() {
    internal object Serializer : SecretsSerializer<FuncSecrets>(::FuncSecrets)
}
