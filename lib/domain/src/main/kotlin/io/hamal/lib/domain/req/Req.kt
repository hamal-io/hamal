package io.hamal.lib.domain.req

import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.StatePayload
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
sealed interface Req {
    val id: ReqId

    //    val partition: Partition
    var status: ReqStatus
}


@Serializable
data class InvokeOneshotReq(
    override val id: ReqId,
    override var status: ReqStatus,
    val execId: ExecId,
    val funcId: FuncId,
    val correlationId: CorrelationId,
    val inputs: InvocationInputs,
    val secrets: InvocationSecrets,
) : Req

@Serializable
data class InvokeFixedRateReq(
    override val id: ReqId,
    override var status: ReqStatus,
    val execId: ExecId,
    val funcId: FuncId,
    val correlationId: CorrelationId,
    val inputs: InvocationInputs,
    val secrets: InvocationSecrets,
) : Req

@Serializable
data class InvokeEventReq(
    override val id: ReqId,
    override var status: ReqStatus,
    val execId: ExecId,
    val funcId: FuncId,
    val correlationId: CorrelationId,
    val inputs: InvocationInputs,
    val secrets: InvocationSecrets,
) : Req


@Serializable
data class CompleteExecReq(
    override val id: ReqId,
    override var status: ReqStatus,
    val execId: ExecId,
    val statePayload: StatePayload
) : Req



