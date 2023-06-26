package io.hamal.lib.sdk.domain

import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
@Deprecated("do not have separate dto")
data class ApiCreateFuncRequest(
    val name: FuncName,
    val inputs: FuncInputs,
    val secrets: FuncSecrets,
    val code: Code
)


@Serializable
@Deprecated("do not have separate dto")
data class ApiExecFuncRequest(
    val correlationId: String? = null, //FIXME vo
    val inputs: InvocationInputs? = null,
    val secrets: InvocationSecrets? = null
)

@Serializable
@Deprecated("do not have separate dto")
data class ApiExecFuncResponse(
    val cmdId: CmdId
)


@Serializable
data class ListFuncsResponse(
    val funcs: List<Func>
) {
    @Serializable
    data class Func(
        val id: FuncId,
        val name: FuncName
    )
}


