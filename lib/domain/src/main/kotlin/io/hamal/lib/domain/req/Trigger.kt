package io.hamal.lib.domain.req

import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
sealed interface CreateTriggerReq {
    val name: TriggerName
    val funcId: FuncId
    val type: TriggerType
    val inputs: TriggerInputs
    val secrets: TriggerSecrets
}

@Serializable
data class CreateFixedRateTrigger(
    override val name: TriggerName,
    override val funcId: FuncId,
    override val inputs: TriggerInputs,
    override val secrets: TriggerSecrets,
    val duration: Duration,
) : CreateTriggerReq {
    override val type = TriggerType.FixedRate
}

@Serializable
sealed interface SubmittedCreateTriggerReq : SubmittedReq {
    val type: TriggerType
    val triggerId: TriggerId
    val triggerName: TriggerName
    val funcId: FuncId
    val inputs: TriggerInputs
    val secrets: TriggerSecrets
}

@Serializable
data class SubmittedCreateFixedRateTriggerReq(
    override val id: ReqId,
    override var status: ReqStatus,
    override val triggerId: TriggerId,
    override val triggerName: TriggerName,
    override val funcId: FuncId,
    override val inputs: TriggerInputs,
    override val secrets: TriggerSecrets,
    val duration: Duration,
) : SubmittedCreateTriggerReq {
    override val type = TriggerType.FixedRate
}


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

