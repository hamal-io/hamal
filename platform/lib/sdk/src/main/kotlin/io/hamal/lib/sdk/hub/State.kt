package io.hamal.lib.sdk.hub

import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.domain.vo.base.Inputs
import io.hamal.lib.domain.vo.base.InputsSerializer
import io.hamal.lib.kua.type.MapType
import kotlinx.serialization.Serializable

@Serializable(with = HubState.Serializer::class)
class HubState(override val value: MapType = MapType()) : Inputs() {
    internal object Serializer : InputsSerializer<HubState>(::HubState)
}

@Serializable
data class HubCorrelation(
    val correlationId: CorrelationId,
    val func: Func
) {
    @Serializable
    data class Func(
        val id: FuncId,
        val name: FuncName
    )
}

@Serializable
data class HubCorrelatedState(
    val correlation: HubCorrelation,
    val state: HubState
) {
    operator fun get(key: String) = state.value[key]
}