package io.hamal.lib.domain.request

import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.vo.*

interface NamespaceAppendRequest {
    val name: NamespaceName
    val features: NamespaceFeaturesMap?
}

data class NamespaceAppendRequested(
    override val requestId: RequestId,
    override val requestedBy: AuthId,
    override var requestStatus: RequestStatus,
    val id: NamespaceId,
    val workspaceId: WorkspaceId,
    val parentId: NamespaceId,
    val name: NamespaceName,
    val features: NamespaceFeaturesMap?
) : Requested()

interface NamespaceUpdateRequest {
    val name: NamespaceName?
    val features: NamespaceFeaturesMap?
}

data class NamespaceUpdateRequested(
    override val requestId: RequestId,
    override val requestedBy: AuthId,
    override var requestStatus: RequestStatus,
    val id: NamespaceId,
    val workspaceId: WorkspaceId,
    val name: NamespaceName?,
    val features: NamespaceFeaturesMap?
) : Requested()
