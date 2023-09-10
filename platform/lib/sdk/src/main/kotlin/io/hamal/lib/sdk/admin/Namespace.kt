package io.hamal.lib.sdk.admin

import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceInputs
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.fold
import kotlinx.serialization.Serializable

@Serializable
data class AdminCreateNamespaceReq(
    val name: NamespaceName,
    val inputs: NamespaceInputs
)

@Serializable
data class AdminUpdateNamespaceReq(
    val name: NamespaceName,
    val inputs: NamespaceInputs,
)

@Serializable
data class AdminNamespaceList(
    val namespaces: List<Namespace>
) {
    @Serializable
    data class Namespace(
        val id: NamespaceId,
        val name: NamespaceName
    )
}

@Serializable
data class AdminNamespace(
    val id: NamespaceId,
    val name: NamespaceName,
    val inputs: NamespaceInputs
)

interface AdminNamespaceService {
    fun create(groupId: GroupId, createNamespaceReq: AdminCreateNamespaceReq): AdminSubmittedReqWithId
    fun list(groupId: GroupId): List<AdminNamespaceList.Namespace>
    fun get(namespaceId: NamespaceId): AdminNamespace
}

internal class DefaultAdminNamespaceService(
    private val template: HttpTemplate
) : AdminNamespaceService {

    override fun create(groupId: GroupId, createNamespaceReq: AdminCreateNamespaceReq) =
        template.post("/v1/groups/{groupId}/namespaces")
            .path("groupId", groupId)
            .body(createNamespaceReq)
            .execute()
            .fold(AdminSubmittedReqWithId::class)

    override fun list(groupId: GroupId) =
        template.get("/v1/groups/{groupId}/namespaces")
            .path("groupId", groupId)
            .execute()
            .fold(AdminNamespaceList::class)
            .namespaces

    override fun get(namespaceId: NamespaceId) =
        template.get("/v1/namespaces/{namespaceId}")
            .path("namespaceId", namespaceId)
            .execute()
            .fold(AdminNamespace::class)

}