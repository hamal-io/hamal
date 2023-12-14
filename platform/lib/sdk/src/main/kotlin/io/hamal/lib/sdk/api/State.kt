package io.hamal.lib.sdk.api

import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.State
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.domain.vo.ReqId
import io.hamal.lib.domain.vo.base.MapValueObject
import io.hamal.lib.domain.vo.base.InputsSerializer
import io.hamal.lib.kua.type.MapType
import io.hamal.request.StateSetReq
import kotlinx.serialization.Serializable

@Serializable
data class ApiStateSetReq(
    override val correlation: Correlation,
    override val value: State
) : StateSetReq

@Serializable
data class ApiStateSetSubmitted(
    override val id: ReqId,
    override val status: ReqStatus
) : ApiSubmitted


@Serializable(with = ApiState.Serializer::class)
class ApiState(override val value: MapType = MapType()) : MapValueObject() {
    internal object Serializer : InputsSerializer<ApiState>(::ApiState)
}

@Serializable
data class ApiCorrelation(
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
data class ApiCorrelatedState(
    val correlation: ApiCorrelation,
    val state: ApiState
) {
    operator fun get(key: String) = state.value[key]
}