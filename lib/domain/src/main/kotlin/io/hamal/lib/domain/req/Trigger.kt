package io.hamal.lib.domain.req

import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
data class InvokeFixedRateReq(
    override val id: ReqId,
    override var status: ReqStatus,
    val execId: ExecId,
    val funcId: FuncId,
    val correlationId: CorrelationId,
    val inputs: InvocationInputs,
    val secrets: InvocationSecrets,
) : SubmittedReq

@Serializable
data class InvokeEventReq(
    override val id: ReqId,
    override var status: ReqStatus,
    val execId: ExecId,
    val funcId: FuncId,
    val correlationId: CorrelationId,
    val inputs: InvocationInputs,
    val secrets: InvocationSecrets,
) : SubmittedReq

