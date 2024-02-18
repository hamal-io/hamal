package io.hamal.lib.domain.request

import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.domain.vo.WorkspaceId

interface NamespaceCreateRequest {
    val name: NamespaceName
}

data class NamespaceAppendRequested(
    override val id: RequestId,
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
    override var status: RequestStatus,
    val workspaceId: WorkspaceId,
    val namespaceId: NamespaceId,
    val name: NamespaceName
) : Requested()
