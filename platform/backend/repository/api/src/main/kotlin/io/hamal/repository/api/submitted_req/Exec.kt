package io.hamal.repository.api.submitted_req

import io.hamal.lib.domain.Event
import io.hamal.lib.domain.EventToSubmit
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.State
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
data class SubmittedInvokeExecReq(
    override val reqId: ReqId,
    override var status: ReqStatus,
    override val groupId: GroupId,
    val id: ExecId,
    val namespaceId: NamespaceId,
    val funcId: FuncId?,
    val correlationId: CorrelationId?,
    val inputs: InvocationInputs,
    val code: ExecCode,
    val events: List<Event>
) : SubmittedReqWithGroupId

@Serializable
data class SubmittedFailExecReq(
    override val reqId: ReqId,
    override var status: ReqStatus,
    val id: ExecId,
    val result: ExecResult
) : SubmittedReq

@Serializable
data class SubmittedCompleteExecReq(
    override val reqId: ReqId,
    override var status: ReqStatus,
    val id: ExecId,
    val state: State,
    val result: ExecResult,
    val events: List<EventToSubmit>
) : SubmittedReq

