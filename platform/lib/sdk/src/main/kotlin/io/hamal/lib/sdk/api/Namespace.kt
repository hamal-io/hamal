package io.hamal.lib.sdk.api

import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.NamespaceCreateRequest
import io.hamal.lib.domain.request.NamespaceUpdateRequest
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.fold

data class ApiNamespaceCreateRequest(
    override val name: NamespaceName
) : NamespaceCreateRequest

data class ApiNamespaceCreateRequested(
    override val id: RequestId,
    override val status: RequestStatus,
    val parentId: NamespaceId,
    val namespaceId: NamespaceId
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
        val name: NamespaceName,
        val children: List<Namespace>
    )
}

data class ApiNamespace(
    val id: NamespaceId,
    val name: NamespaceName,
    val children: List<ApiNamespace>
) : ApiObject()

interface ApiNamespaceService {
    fun create(workspaceId: WorkspaceId, createNamespaceReq: ApiNamespaceCreateRequest): ApiNamespaceCreateRequested
    fun list(workspaceId: WorkspaceId): List<ApiNamespaceList.Namespace>
    fun get(namespaceId: NamespaceId): ApiNamespace
}

internal class ApiNamespaceServiceImpl(
    private val template: HttpTemplate
) : ApiNamespaceService {

    override fun create(
        workspaceId: WorkspaceId,
        createNamespaceReq: ApiNamespaceCreateRequest
    ): ApiNamespaceCreateRequested =
        template.post("/v1/workspaces/{workspaceId}/namespaces")
            .path("workspaceId", workspaceId)
            .body(createNamespaceReq)
            .execute()
            .fold(ApiNamespaceCreateRequested::class)

    override fun list(workspaceId: WorkspaceId) =
        template.get("/v1/workspaces/{workspaceId}/namespaces")
            .path("workspaceId", workspaceId)
            .execute()
            .fold(ApiNamespaceList::class)
            .namespaces

    override fun get(namespaceId: NamespaceId) =
        template.get("/v1/namespaces/{namespaceId}")
            .path("namespaceId", namespaceId)
            .execute()
            .fold(ApiNamespace::class)
}