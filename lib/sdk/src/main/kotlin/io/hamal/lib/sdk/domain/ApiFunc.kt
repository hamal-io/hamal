package io.hamal.lib.sdk.domain

import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.FuncName
import kotlinx.serialization.Serializable

@Serializable
data class ApiCreateFuncRequest(
    val name: FuncName,
    val code: Code
)


@Serializable
data class ApiExecFuncRequest(
    val correlationId: String? = null //FIXME vo
)

@Serializable
data class ApiExecFuncResponse(
    val reqId: ReqId
)


@Serializable
data class ApiListFuncResponse(
    val funcs: List<Func>
) {

    @Serializable
    data class Func(
        val id: FuncId,
        val name: FuncName
    )
}


