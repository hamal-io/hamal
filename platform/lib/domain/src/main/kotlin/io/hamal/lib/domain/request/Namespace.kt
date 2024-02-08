package io.hamal.lib.domain.request

import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.vo.*

interface NamespaceCreateRequest {
    val name: NamespaceName
    val inputs: NamespaceInputs
    val type: NamespaceType?
}

data class NamespaceCreateRequested(
    override val id: RequestId,
    override var status: RequestStatus,
    val groupId: GroupId,
    val namespaceId: NamespaceId,
    val name: NamespaceName,
    val inputs: NamespaceInputs,
    val namespaceType: NamespaceType
) : Requested()

interface NamespaceUpdateRequest {
    val name: NamespaceName
    val inputs: NamespaceInputs
}


data class NamespaceUpdateRequested(
    override val id: RequestId,
    override var status: RequestStatus,
    val groupId: GroupId,
    val namespaceId: NamespaceId,
    val name: NamespaceName,
    val inputs: NamespaceInputs
) : Requested()
