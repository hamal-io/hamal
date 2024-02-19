package io.hamal.lib.domain.request

import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.vo.*

interface HookCreateRequest {
    val name: HookName
}

data class HookCreateRequested(
    override val id: RequestId,
    override var status: RequestStatus,
    val hookId: HookId,
    val namespaceId: NamespaceId,
    val workspaceId: WorkspaceId,
    val name: HookName,
) : Requested()


interface HookUpdateRequest {
    val name: HookName?
}

data class HookUpdateRequested(
    override val id: RequestId,
    override var status: RequestStatus,
    val hookId: HookId,
    val workspaceId: WorkspaceId,
    val name: HookName?,
) : Requested()

data class HookInvokeRequested(
    override val id: RequestId,
    override var status: RequestStatus,
    val hookId: HookId,
    val workspaceId: WorkspaceId,
    val invocation: Invocation.Hook
) : Requested()
