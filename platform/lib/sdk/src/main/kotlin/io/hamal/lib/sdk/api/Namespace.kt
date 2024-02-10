package io.hamal.lib.sdk.api

import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.NamespaceCreateRequest
import io.hamal.lib.domain.request.NamespaceUpdateRequest
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.fold

data class ApiNamespaceCreateRequest(
    override val name: NamespaceName
) : NamespaceCreateRequest

data class ApiNamespaceCreateRequested(
    override val id: RequestId,
    override val status: RequestStatus,
    val namespaceId: NamespaceId,
    val groupId: GroupId,
) : ApiRequested()

data class ApiNamespaceUpdateRequest(
    override val name: NamespaceName,
) : NamespaceUpdateRequest

data class ApiNamespaceUpdateRequested(
    override val id: RequestId,
    override val status: RequestStatus,
    val namespaceId: NamespaceId,
) : ApiRequested()

data class ApiNamespaceList(
    val namespaces: List<Namespace>
) : ApiObject() {
    data class Namespace(
        val id: NamespaceId,
        val name: NamespaceName
    )
}

data class ApiNamespace(
    val id: NamespaceId,
    val name: NamespaceName,
) : ApiObject()

interface ApiNamespaceService {
    fun create(groupId: GroupId, createNamespaceReq: ApiNamespaceCreateRequest): ApiNamespaceCreateRequested
    fun list(groupId: GroupId): List<ApiNamespaceList.Namespace>
    fun get(namespaceId: NamespaceId): ApiNamespace
}

internal class ApiNamespaceServiceImpl(
    private val template: HttpTemplate
) : ApiNamespaceService {

    override fun create(groupId: GroupId, createNamespaceReq: ApiNamespaceCreateRequest): ApiNamespaceCreateRequested =
        template.post("/v1/groups/{groupId}/namespaces")
            .path("groupId", groupId)
            .body(createNamespaceReq)
            .execute()
            .fold(ApiNamespaceCreateRequested::class)

    override fun list(groupId: GroupId) =
        template.get("/v1/groups/{groupId}/namespaces")
            .path("groupId", groupId)
            .execute()
            .fold(ApiNamespaceList::class)
            .namespaces

    override fun get(namespaceId: NamespaceId) =
        template.get("/v1/namespaces/{namespaceId}")
            .path("namespaceId", namespaceId)
            .execute()
            .fold(ApiNamespace::class)
}