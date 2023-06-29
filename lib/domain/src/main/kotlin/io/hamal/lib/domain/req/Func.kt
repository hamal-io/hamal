package io.hamal.lib.domain.req

import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
data class CreateFuncReq(
    val name: FuncName,
    val inputs: FuncInputs,
    val code: Code
)

@Serializable
data class SubmittedCreateFuncReq(
    override val id: ReqId,
    override var status: ReqStatus,
    val funcId: FuncId,
    val funcName: FuncName,
    val inputs: FuncInputs,
    val code: Code
) : SubmittedReq

@Serializable
data class InvokeOneshotReq(
    val correlationId: CorrelationId,
    val inputs: InvocationInputs,
)

@Serializable
data class SubmittedInvokeOneshotReq(
    override val id: ReqId,
    override var status: ReqStatus,
    val execId: ExecId,
    val funcId: FuncId,
    val correlationId: CorrelationId,
    val inputs: InvocationInputs,
) : SubmittedReq