package io.hamal.lib.domain.request

import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.vo.*

interface NamespaceAppendRequest {
    val name: NamespaceName
}

data class NamespaceAppendRequested(
    override val id: RequestId,
    override val by: AuthId,
    override var status: RequestStatus,
    val workspaceId: WorkspaceId,
    val parentId: NamespaceId,
    val namespaceId: NamespaceId,
    val name: NamespaceName
) : Requested()

interface NamespaceUpdateRequest {
    val name: NamespaceName
}


data class NamespaceUpdateRequested(
    override val id: RequestId,
    override val by: AuthId,
    override var status: RequestStatus,
    val workspaceId: WorkspaceId,
    val namespaceId: NamespaceId,
    val name: NamespaceName
) : Requested()
