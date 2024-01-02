package io.hamal.lib.domain

import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.base.MapValueObject
import io.hamal.lib.kua.type.MapType

class State(override val value: MapType = MapType()) : MapValueObject()

data class Correlation(
    val correlationId: CorrelationId,
    val funcId: FuncId
)

data class CorrelatedState(
    val correlation: Correlation,
    val value: State
) {
    operator fun get(key: String) = value.value[key]
}