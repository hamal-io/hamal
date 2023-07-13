package io.hamal.lib.domain

import io.hamal.lib.domain.vo.base.Inputs
import io.hamal.lib.domain.vo.base.InputsSerializer
import io.hamal.lib.script.api.value.TableValue
import kotlinx.serialization.Serializable

@Serializable(with = State.Serializer::class)
class State(override val value: TableValue = TableValue()) : Inputs() {
    internal object Serializer : InputsSerializer<State>(::State)
}

@Serializable
data class CorrelatedState(
    val correlation: Correlation,
    val state: State
)