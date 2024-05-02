package io.hamal.lib.sdk.api

import io.hamal.lib.common.value.Value
import io.hamal.lib.common.value.ValueObject
import io.hamal.lib.common.value.ValueVariableObject
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.State
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.StateSetRequest
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.domain.vo.RequestId

data class ApiStateSetRequest(
    override val correlation: Correlation,
    override val value: State
) : StateSetRequest

data class ApiStateSetRequested(
    override val requestId: RequestId,
    override val requestStatus: RequestStatus
) : ApiRequested()


class ApiState(override val value: ValueObject = ValueObject.empty) : ValueVariableObject()

data class ApiCorrelation(
    val id: CorrelationId,
    val func: Func
) {
    data class Func(
        val id: FuncId,
        val name: FuncName
    )
}

data class ApiCorrelatedState(
    val correlation: ApiCorrelation,
    val state: ApiState
) {
    operator fun get(key: String): Value = state.value[key]
}