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
    val duration: Duration? = null,
    val topicId: TopicId? = null,
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
    val duration: Duration? = null,
    val topicId: TopicId? = null
) : SubmittedReq


@Serializable
data class SubmittedInvokeFixedRateReq(
    override val id: ReqId,
    override var status: ReqStatus,
    val execId: ExecId,
    val funcId: FuncId,
    val correlationId: CorrelationId,
    val inputs: InvocationInputs,
) : SubmittedReq

@Serializable
data class SubmittedInvokeEventReq(
    override val id: ReqId,
    override var status: ReqStatus,
    val execId: ExecId,
    val funcId: FuncId,
    val correlationId: CorrelationId,
    val inputs: InvocationInputs,
) : SubmittedReq

