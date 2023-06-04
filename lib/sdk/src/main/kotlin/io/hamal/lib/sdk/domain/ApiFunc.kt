package io.hamal.lib.sdk.domain

import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
data class ApiCreateFuncRequest(
    val name: FuncName,
    val code: Code
)


@Serializable
data class ApiExecFuncRequest(
    val correlationId: String? = null, //FIXME vo
    val inputs: InvocationInputs? = null,
    val secrets: InvocationSecrets? = null
)

@Serializable
data class ApiExecFuncResponse(
    val cmdId: CmdId
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


