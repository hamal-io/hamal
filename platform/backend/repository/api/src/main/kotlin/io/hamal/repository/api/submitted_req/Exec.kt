package io.hamal.repository.api.submitted_req

import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
data class ExecInvokeSubmitted(
    override val id: ReqId,
    override var status: ReqStatus,
    val groupId: GroupId,
    val execId: ExecId,
    val namespaceId: NamespaceId,
    val funcId: FuncId?,
    val correlationId: CorrelationId?,
    val inputs: InvocationInputs,
    val code: ExecCode,
    val events: List<Event>
) : Submitted

@Serializable
data class ExecFailSubmitted(
    override val id: ReqId,
    override var status: ReqStatus,
    val execId: ExecId,
    val result: ExecResult
) : Submitted

@Serializable
data class ExecCompleteSubmitted(
    override val id: ReqId,
    override var status: ReqStatus,
    val execId: ExecId,
    val state: ExecState,
    val result: ExecResult,
    val events: List<EventToSubmit>
) : Submitted

