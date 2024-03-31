package io.hamal.lib.domain.request

import io.hamal.lib.domain.EventToSubmit
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.vo.*

data class ExecInvokeRequested(
    override val requestId: RequestId,
    override val requestedBy: AuthId,
    override var requestStatus: RequestStatus,
    val id: ExecId,
    val triggerId: TriggerId?,
    val workspaceId: WorkspaceId,
    val namespaceId: NamespaceId,
    val funcId: FuncId?,
    val correlationId: CorrelationId?,
    val inputs: InvocationInputs,
    val code: ExecCode
) : Requested()


interface ExecFailRequest {
    val result: ExecResult
}

data class ExecFailRequested(
    override val requestId: RequestId,
    override val requestedBy: AuthId,
    override var requestStatus: RequestStatus,
    val id: ExecId,
    val result: ExecResult
) : Requested()


interface ExecCompleteRequest {
    val result: ExecResult
    val state: ExecState
    val events: List<EventToSubmit>
}

data class ExecCompleteRequested(
    override val requestId: RequestId,
    override val requestedBy: AuthId,
    override var requestStatus: RequestStatus,
    val id: ExecId,
    val state: ExecState,
    val result: ExecResult,
    val events: List<EventToSubmit>
) : Requested()

