package io.hamal.lib.domain.req

import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.kua.value.CodeValue
import kotlinx.serialization.Serializable

@Serializable
data class InvokeAdhocReq(
    val inputs: InvocationInputs,
    val code: CodeValue
)

//@Serializable
//data class SubmittedInvokeAdhocReq(
//    override val reqId: ReqId,
//    override var status: ReqStatus,
//    val id: ExecId,
//    val inputs: InvocationInputs,
//    val code: CodeValue
//) : SubmittedReq
