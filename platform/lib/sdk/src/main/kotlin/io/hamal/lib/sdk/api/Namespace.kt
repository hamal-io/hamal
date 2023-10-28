package io.hamal.lib.sdk.api

import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceInputs
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.http.body
import io.hamal.lib.sdk.fold
import io.hamal.lib.sdk.foldReq
import io.hamal.request.CreateNamespaceReq
import io.hamal.request.UpdateNamespaceReq
import kotlinx.serialization.Serializable

@Serializable
data class ApiNamespaceCreateReq(
    override val name: NamespaceName,
    override val inputs: NamespaceInputs
) : CreateNamespaceReq

@Serializable
data class ApiNamespaceUpdateReq(
    override val name: NamespaceName,
    override val inputs: NamespaceInputs,
) : UpdateNamespaceReq

@Serializable
data class ApiNamespaceList(
    val namespaces: List<Namespace>
) {
    @Serializable
    data class Namespace(
        val id: NamespaceId,
        val name: NamespaceName
    )
}

@Serializable
data class ApiNamespace(
    val id: NamespaceId,
    val name: NamespaceName,
    val inputs: NamespaceInputs
)

interface ApiNamespaceService {
    fun create(groupId: GroupId, createNamespaceReq: ApiNamespaceCreateReq): ApiSubmittedReqImpl<NamespaceId>
    fun list(groupId: GroupId): List<ApiNamespaceList.Namespace>
    fun get(namespaceId: NamespaceId): ApiNamespace
}

internal class ApiNamespaceServiceImpl(
    private val template: HttpTemplateImpl
) : ApiNamespaceService {

    override fun create(groupId: GroupId, createNamespaceReq: ApiNamespaceCreateReq): ApiSubmittedReqImpl<NamespaceId> =
        template.post("/v1/groups/{groupId}/namespaces")
            .path("groupId", groupId)
            .body(createNamespaceReq)
            .execute()
            .foldReq()

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