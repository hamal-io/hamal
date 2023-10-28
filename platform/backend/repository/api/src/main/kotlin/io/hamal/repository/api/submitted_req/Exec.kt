package io.hamal.repository.api.submitted_req

import io.hamal.lib.domain.State
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
data class ExecInvokeSubmitted(
    override val reqId: ReqId,
    override var status: ReqStatus,
    val groupId: GroupId,
    val id: ExecId,
    val namespaceId: NamespaceId,
    val funcId: FuncId?,
    val correlationId: CorrelationId?,
    val inputs: InvocationInputs,
    val code: ExecCode,
    val events: List<Event>
) : Submitted

@Serializable
data class ExecFailSubmitted(
    override val reqId: ReqId,
    override var status: ReqStatus,
    val id: ExecId,
    val result: ExecResult
) : Submitted

@Serializable
data class ExecCompleteSubmitted(
    override val reqId: ReqId,
    override var status: ReqStatus,
    val id: ExecId,
    val state: State,
    val result: ExecResult,
    val events: List<EventToSubmit>
) : Submitted

