package io.hamal.lib.sdk.api

import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.NamespaceCreateRequest
import io.hamal.lib.domain.request.NamespaceUpdateRequest
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.fold

data class ApiNamespaceCreateRequest(
    override val name: NamespaceName,
    override val inputs: NamespaceInputs,
    override val type: NamespaceType? = null
) : NamespaceCreateRequest

data class ApiNamespaceCreateRequested(
    override val id: RequestId,
    override val status: RequestStatus,
    val namespaceId: NamespaceId,
    val groupId: GroupId,
) : ApiRequested()

data class ApiNamespaceUpdateRequest(
    override val name: NamespaceName,
    override val inputs: NamespaceInputs,
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
        val type: NamespaceType,
        val id: NamespaceId,
        val name: NamespaceName
    )
}

data class ApiNamespace(
    val id: NamespaceId,
    val type: NamespaceType,
    val name: NamespaceName,
    val inputs: NamespaceInputs
) : ApiObject()

interface ApiNamespaceservice {
    fun create(groupId: GroupId, createNamespaceReq: ApiNamespaceCreateRequest): ApiNamespaceCreateRequested
    fun list(groupId: GroupId): List<ApiNamespaceList.Namespace>
    fun get(namespaceId: NamespaceId): ApiNamespace
}

internal class ApiNamespaceserviceImpl(
    private val template: HttpTemplate
) : ApiNamespaceservice {

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