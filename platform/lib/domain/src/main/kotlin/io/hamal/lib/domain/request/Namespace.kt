package io.hamal.lib.domain.request

import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.vo.*

interface NamespaceAppendRequest {
    val name: NamespaceName
    val features: NamespaceFeatures?
}

data class NamespaceAppendRequested(
    override val requestId: RequestId,
    override val requestedBy: AuthId,
    override var requestStatus: RequestStatus,
    val id: NamespaceId,
    val workspaceId: WorkspaceId,
    val parentId: NamespaceId,
    val name: NamespaceName,
    val features: NamespaceFeatures?
) : Requested()

interface NamespaceUpdateRequest {
    val name: NamespaceName?
    val features: NamespaceFeatures?
}

data class NamespaceUpdateRequested(
    override val requestId: RequestId,
    override val requestedBy: AuthId,
    override var requestStatus: RequestStatus,
    val id: NamespaceId,
    val workspaceId: WorkspaceId,
    val name: NamespaceName?,
    val features: NamespaceFeatures?
) : Requested()


data class NamespaceDeleteRequested(
    override val requestId: RequestId,
    override val requestedBy: AuthId,
    override var requestStatus: RequestStatus,
    val id: NamespaceId
) : Requested()
