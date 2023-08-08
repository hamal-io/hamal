package io.hamal.lib.domain.req

import io.hamal.lib.domain.Invocation
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.value.CodeValue
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class CreateTriggerReq(
    val type: TriggerType,
    val name: TriggerName,
    val funcId: FuncId,
    val inputs: TriggerInputs,
    val correlationId: CorrelationId? = null,
    val duration: Duration? = null,
    val topicId: TopicId? = null,
)

@Serializable
data class SubmittedCreateTriggerReq(
    override val reqId: ReqId,
    override var status: ReqStatus,
    val type: TriggerType,
    val id: TriggerId,
    val name: TriggerName,
    val funcId: FuncId,
    val inputs: TriggerInputs,
    val correlationId: CorrelationId? = null,
    val duration: Duration? = null,
    val topicId: TopicId? = null
) : SubmittedReq


@Serializable
data class SubmittedInvokeExecReq(
    override val reqId: ReqId,
    override var status: ReqStatus,
    val id: ExecId,
    val funcId: FuncId?,
    val correlationId: CorrelationId?,
    val inputs: InvocationInputs,
    val invocation: Invocation,
    val code: CodeValue?
) : SubmittedReq
