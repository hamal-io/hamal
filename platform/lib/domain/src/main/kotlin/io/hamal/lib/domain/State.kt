package io.hamal.lib.domain

import io.hamal.lib.common.value.Value
import io.hamal.lib.common.value.ValueObject
import io.hamal.lib.common.value.ValueVariableObject
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncId

class State(override val value: ValueObject = ValueObject.empty) : ValueVariableObject()

data class Correlation(
    val id: CorrelationId,
    val funcId: FuncId
)

data class CorrelatedState(
    val correlation: Correlation,
    val value: State
) {
    operator fun get(key: String): Value = value.value[key]
}