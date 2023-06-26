package io.hamal.lib.domain.req

import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
data class CreateFuncReq(
    val name: FuncName,
    val inputs: FuncInputs,
    val secrets: FuncSecrets,
    val code: Code
)

@Serializable
data class SubmittedCreateFuncReq(
    override val id: ReqId,
    override var status: ReqStatus,
    val funcId: FuncId,
    val funcName: FuncName,
    val inputs: FuncInputs,
    val secrets: FuncSecrets,
    val code: Code
) : SubmittedReq