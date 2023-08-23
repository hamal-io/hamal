package io.hamal.lib.domain.vo

import io.hamal.lib.domain.vo.base.Inputs
import io.hamal.lib.domain.vo.base.InputsSerializer
import io.hamal.lib.kua.type.DepTableType
import kotlinx.serialization.Serializable

@Serializable(with = InvocationInputs.Serializer::class)
class InvocationInputs(override val value: DepTableType = DepTableType()) : Inputs() {
    internal object Serializer : InputsSerializer<InvocationInputs>(::InvocationInputs)
}