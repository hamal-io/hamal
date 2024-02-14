package io.hamal.lib.domain.request

import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.vo.*


interface FuncCreateRequest {
    val name: FuncName
    val inputs: FuncInputs
    val code: CodeValue
}

data class FuncCreateRequested(
    override val id: RequestId,
    override var status: RequestStatus,
    val workspaceId: WorkspaceId,
    val funcId: FuncId,
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
    override val id: RequestId,
    override var status: RequestStatus,
    val workspaceId: WorkspaceId,
    val funcId: FuncId,
    val name: FuncName?,
    val inputs: FuncInputs?,
    val code: CodeValue?
) : Requested()


interface FuncInvokeRequest {
    val correlationId: CorrelationId?
    val inputs: InvocationInputs
    val invocation: Invocation
}

interface FuncInvokeVersionRequest {
    val correlationId: CorrelationId?
    val inputs: InvocationInputs
    val version: CodeVersion?
}

interface FuncDeployRequest {
    val version: CodeVersion?
    val message: DeployMessage?
}

data class FuncDeployRequested(
    override val id: RequestId,
    override var status: RequestStatus,
    val workspaceId: WorkspaceId,
    val funcId: FuncId,
    val version: CodeVersion?,
    val message: DeployMessage?
) : Requested()