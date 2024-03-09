package io.hamal.lib.domain.request

import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.vo.*


interface FuncCreateRequest {
    val name: FuncName
    val inputs: FuncInputs
    val code: CodeValue
}

data class FuncCreateRequested(
    override val requestId: RequestId,
    override val requestedBy: AuthId,
    override var requestStatus: RequestStatus,
    val id: FuncId,
    val workspaceId: WorkspaceId,
    val namespaceId: NamespaceId,
    val name: FuncName,
    val inputs: FuncInputs,
    val codeId: CodeId,
    val code: CodeValue
) : Requested()

interface FuncUpdateRequest {
    val name: FuncName?
    val inputs: FuncInputs?
    val code: CodeValue?
}

data class FuncUpdateRequested(
    override val requestId: RequestId,
    override val requestedBy: AuthId,
    override var requestStatus: RequestStatus,
    val id: FuncId,
    val workspaceId: WorkspaceId,
    val name: FuncName?,
    val inputs: FuncInputs?,
    val code: CodeValue?
) : Requested()


interface FuncInvokeRequest {
    val correlationId: CorrelationId?
    val inputs: InvocationInputs?
    val version: CodeVersion?
}

interface FuncDeployRequest {
    val version: CodeVersion?
    val message: DeployMessage?
}

data class FuncDeployRequested(
    override val requestId: RequestId,
    override val requestedBy: AuthId,
    override var requestStatus: RequestStatus,
    val id: FuncId,
    val workspaceId: WorkspaceId,
    val version: CodeVersion?,
    val message: DeployMessage?
) : Requested()