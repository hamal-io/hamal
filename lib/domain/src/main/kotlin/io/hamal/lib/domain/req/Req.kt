package io.hamal.lib.domain.req

import io.hamal.lib.common.Shard
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.StatePayload
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
sealed interface Req {
    val id: ReqId
    val shard: Shard
    var status: ReqStatus
}

@Serializable
data class SubmittedInvokeAdhocReq(
    override val id: ReqId,
    override var status: ReqStatus,
    val execId: ExecId,
    val tenantId: TenantId,
    val inputs: InvocationInputs,
    val secrets: InvocationSecrets,
    val code: Code
) : Req {
    override val shard = execId.shard()
}

@Serializable
data class InvokeOneshotReq(
    override val id: ReqId,
    override var status: ReqStatus,
    val execId: ExecId,
    val tenantId: TenantId,
    val funcId: FuncId,
    val correlationId: CorrelationId,
    val inputs: InvocationInputs,
    val secrets: InvocationSecrets,
) : Req {
    override val shard = execId.shard()
}

@Serializable
data class InvokeFixedRateReq(
    override val id: ReqId,
    override var status: ReqStatus,
    val execId: ExecId,
    val tenantId: TenantId,
    val funcId: FuncId,
    val correlationId: CorrelationId,
    val inputs: InvocationInputs,
    val secrets: InvocationSecrets,
) : Req {
    override val shard = execId.shard()
}

@Serializable
data class InvokeEventReq(
    override val id: ReqId,
    override var status: ReqStatus,
    val execId: ExecId,
    val tenantId: TenantId,
    val funcId: FuncId,
    val correlationId: CorrelationId,
    val inputs: InvocationInputs,
    val secrets: InvocationSecrets,
) : Req {
    override val shard = execId.shard()
}


@Serializable
data class CompleteExecReq(
    override val id: ReqId,
    override var status: ReqStatus,
    val execId: ExecId,
    val statePayload: StatePayload
) : Req {
    override val shard = execId.shard()
}



