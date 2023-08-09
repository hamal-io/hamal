package io.hamal.lib.domain.req

import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.value.CodeValue
import kotlinx.serialization.Serializable

@Serializable
data class CreateFuncReq(
    val name: FuncName,
    val inputs: FuncInputs,
    val code: CodeValue
)

@Serializable
data class SubmittedCreateFuncReq(
    override val reqId: ReqId,
    override var status: ReqStatus,
    val id: FuncId,
    val name: FuncName,
    val inputs: FuncInputs,
    val code: CodeValue
) : SubmittedReq

@Serializable
data class UpdateFuncReq(
    val name: FuncName,
    val inputs: FuncInputs,
    val code: CodeValue
)

@Serializable
data class SubmittedUpdateFuncReq(
    override val reqId: ReqId,
    override var status: ReqStatus,
    val id: FuncId,
    val name: FuncName,
    val inputs: FuncInputs,
    val code: CodeValue
) : SubmittedReq

@Serializable
data class InvokeFuncReq(
    val correlationId: CorrelationId?,
    val inputs: InvocationInputs?,
)
