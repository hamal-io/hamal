package io.hamal.lib.domain.request

import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.vo.*

interface HookCreateRequest {
    val name: HookName
}

data class HookCreateRequested(
    override val requestId: RequestId,
    override val requestedBy: AuthId,
    override var requestStatus: RequestStatus,
    val id: HookId,
    val namespaceId: NamespaceId,
    val workspaceId: WorkspaceId,
    val name: HookName,
) : Requested()


interface HookUpdateRequest {
    val name: HookName?
}

data class HookUpdateRequested(
    override val requestId: RequestId,
    override val requestedBy: AuthId,
    override var requestStatus: RequestStatus,
    val id: HookId,
    val workspaceId: WorkspaceId,
    val name: HookName?,
) : Requested()

data class HookInvokeRequested(
    override val requestId: RequestId,
    override val requestedBy: AuthId,
    override var requestStatus: RequestStatus,
    val id: HookId,
    val workspaceId: WorkspaceId,
    val inputs: InvocationInputs
) : Requested()
