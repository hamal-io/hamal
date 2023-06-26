package io.hamal.lib.domain.req

import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class CreateTriggerReq(
    val type: TriggerType,
    val name: TriggerName,
    val funcId: FuncId,
    val inputs: TriggerInputs,
    val secrets: TriggerSecrets,
    val duration: Duration? = null,
)

@Serializable
data class SubmittedCreateTriggerReq(
    override val id: ReqId,
    override var status: ReqStatus,
    val type: TriggerType,
    val triggerId: TriggerId,
    val triggerName: TriggerName,
    val funcId: FuncId,
    val inputs: TriggerInputs,
    val secrets: TriggerSecrets,
    val duration: Duration? = null,
) : SubmittedReq


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

