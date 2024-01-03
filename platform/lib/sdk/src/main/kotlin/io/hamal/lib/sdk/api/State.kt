package io.hamal.lib.sdk.api

import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.State
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.domain.vo.base.MapValueObject
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.domain.request.StateSetRequest

data class ApiStateSetRequest(
    override val correlation: Correlation,
    override val value: State
) : StateSetRequest

data class ApiStateSetRequested(
    override val id: RequestId,
    override val status: RequestStatus
) : ApiRequested


class ApiState(override val value: MapType = MapType()) : MapValueObject()

data class ApiCorrelation(
    val correlationId: CorrelationId,
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
    operator fun get(key: String) = state.value[key]
}