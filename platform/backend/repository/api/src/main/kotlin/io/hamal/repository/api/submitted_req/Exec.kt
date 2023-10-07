package io.hamal.repository.api.submitted_req

import io.hamal.lib.domain.Event
import io.hamal.lib.domain.EventToSubmit
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.State
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.type.ErrorType
import kotlinx.serialization.Serializable

@Serializable
data class SubmittedInvokeExecReq(
    override val reqId: ReqId,
    override var status: ReqStatus,
    override val groupId: GroupId,
    val id: ExecId,
    val funcId: FuncId?,
    val correlationId: CorrelationId?,
    val inputs: InvocationInputs,
    val code: CodeValue?,
    val codeId: CodeId?,
    val codeVersion: CodeVersion?,
    val events: List<Event>
) : SubmittedReqWithGroupId

@Serializable
data class SubmittedFailExecReq(
    override val reqId: ReqId,
    override var status: ReqStatus,
    override val groupId: GroupId,
    val id: ExecId,
    val cause: ErrorType
) : SubmittedReqWithGroupId

@Serializable
data class SubmittedCompleteExecReq(
    override val reqId: ReqId,
    override var status: ReqStatus,
    override val groupId: GroupId,
    val id: ExecId,
    val state: State,
    val events: List<EventToSubmit>
) : SubmittedReqWithGroupId

