package io.hamal.lib.domain

import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.base.InputsSerializer
import io.hamal.lib.domain.vo.base.MapValueObject
import io.hamal.lib.kua.type.MapType
import kotlinx.serialization.Serializable

@Serializable(with = State.Serializer::class)
class State(override val value: MapType = MapType()) : MapValueObject() {
    internal object Serializer : InputsSerializer<State>(::State)
}

@Serializable
data class Correlation(
    val correlationId: CorrelationId,
    val funcId: FuncId
)

@Serializable
data class CorrelatedState(
    val correlation: Correlation,
    val value: State
) {
    operator fun get(key: String) = value.value[key]
}