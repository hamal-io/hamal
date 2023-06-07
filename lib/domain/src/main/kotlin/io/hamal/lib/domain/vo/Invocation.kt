package io.hamal.lib.domain.vo

import io.hamal.lib.common.value.TableValue
import io.hamal.lib.domain.vo.base.*
import kotlinx.serialization.Serializable

@Serializable(with = InvocationInputs.Serializer::class)
class InvocationInputs(override val value: TableValue) : Inputs() {
    internal object Serializer : InputsSerializer<InvocationInputs>(::InvocationInputs)
}


@Serializable(with = InvocationSecrets.Serializer::class)
class InvocationSecrets(override val value: List<Secret>) : Secrets() {
    internal object Serializer : SecretsSerializer<InvocationSecrets>(::InvocationSecrets)
}
