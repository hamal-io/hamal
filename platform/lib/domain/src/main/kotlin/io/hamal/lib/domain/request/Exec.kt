package io.hamal.lib.domain.request

import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.vo.*

data class ExecInvokeRequested(
    override val id: RequestId,
    override val by: AuthId,
    override var status: RequestStatus,
    val workspaceId: WorkspaceId,
    val execId: ExecId,
    val namespaceId: NamespaceId,
    val funcId: FuncId?,
    val correlationId: CorrelationId?,
    val inputs: InvocationInputs,
    val code: ExecCode,
    val invocation: Invocation
) : Requested()


interface ExecFailRequest {
    val result: ExecResult
}

data class ExecFailRequested(
    override val id: RequestId,
    override val by: AuthId,
    override var status: RequestStatus,
    val execId: ExecId,
    val result: ExecResult
) : Requested()


interface ExecCompleteRequest {
    val result: ExecResult
    val state: ExecState
    val events: List<EventToSubmit>
}

data class ExecCompleteRequested(
    override val id: RequestId,
    override val by: AuthId,
    override var status: RequestStatus,
    val execId: ExecId,
    val state: ExecState,
    val result: ExecResult,
    val events: List<EventToSubmit>
) : Requested()

