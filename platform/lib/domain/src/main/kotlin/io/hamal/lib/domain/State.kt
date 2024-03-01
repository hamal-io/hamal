package io.hamal.lib.domain

import io.hamal.lib.common.domain.ValueObjectHotObject
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncId

class State(override val value: HotObject = HotObject.empty) : ValueObjectHotObject()

data class Correlation(
    val id: CorrelationId,
    val funcId: FuncId
)

data class CorrelatedState(
    val correlation: Correlation,
    val value: State
) {
    operator fun get(key: String) = value.value[key]
}