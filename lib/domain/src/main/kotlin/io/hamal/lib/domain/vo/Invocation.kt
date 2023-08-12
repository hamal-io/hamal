package io.hamal.lib.domain.vo

import io.hamal.lib.domain.vo.base.Inputs
import io.hamal.lib.domain.vo.base.InputsSerializer
import io.hamal.lib.kua.type.TableType
import kotlinx.serialization.Serializable

@Serializable(with = InvocationInputs.Serializer::class)
class InvocationInputs(override val value: TableType = TableType()) : Inputs() {
    internal object Serializer : InputsSerializer<InvocationInputs>(::InvocationInputs)
}