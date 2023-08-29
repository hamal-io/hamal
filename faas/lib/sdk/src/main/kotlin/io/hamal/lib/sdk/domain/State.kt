package io.hamal.lib.sdk.domain

import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.domain.vo.base.Inputs
import io.hamal.lib.domain.vo.base.InputsSerializer
import io.hamal.lib.kua.type.MapType
import kotlinx.serialization.Serializable

@Serializable(with = ApiState.Serializer::class)
class ApiState(override val value: MapType = MapType()) : Inputs() {
    internal object Serializer : InputsSerializer<ApiState>(::ApiState)
}

@Serializable
data class ApiCorrelation(
    val correlationId: CorrelationId,
    val func: ApiFunc
) {
    @Serializable
    data class ApiFunc(
        val id: FuncId,
        val name: FuncName
    )
}

@Serializable
data class ApiCorrelatedState(
    val correlation: ApiCorrelation,
    val value: ApiState
) {
    operator fun get(key: String) = value.value[key]
}