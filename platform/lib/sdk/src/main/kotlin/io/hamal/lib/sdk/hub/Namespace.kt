package io.hamal.lib.sdk.hub

import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceInputs
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.fold
import io.hamal.request.CreateNamespaceReq
import io.hamal.request.UpdateNamespaceReq
import kotlinx.serialization.Serializable

@Serializable
data class HubCreateNamespaceReq(
    override val name: NamespaceName,
    override val inputs: NamespaceInputs
) : CreateNamespaceReq

@Serializable
data class HubUpdateNamespaceReq(
    override val name: NamespaceName,
    override val inputs: NamespaceInputs,
) : UpdateNamespaceReq

@Serializable
data class HubNamespaceList(
    val namespaces: List<Namespace>
) {
    @Serializable
    data class Namespace(
        val id: NamespaceId,
        val name: NamespaceName
    )
}

@Serializable
data class HubNamespace(
    val id: NamespaceId,
    val name: NamespaceName,
    val inputs: NamespaceInputs
)

interface HubNamespaceService {
    fun create(groupId: GroupId, createNamespaceReq: HubCreateNamespaceReq): HubSubmittedReqWithId
    fun list(groupId: GroupId): List<HubNamespaceList.Namespace>
    fun get(namespaceId: NamespaceId): HubNamespace
}

internal class DefaultHubNamespaceService(
    private val template: HttpTemplate
) : HubNamespaceService {

    override fun create(groupId: GroupId, createNamespaceReq: HubCreateNamespaceReq) =
        template.post("/v1/groups/{groupId}/namespaces")
            .path("groupId", groupId)
            .body(createNamespaceReq)
            .execute()
            .fold(HubSubmittedReqWithId::class)

    override fun list(groupId: GroupId) =
        template.get("/v1/groups/{groupId}/namespaces")
            .path("groupId", groupId)
            .execute()
            .fold(HubNamespaceList::class)
            .namespaces

    override fun get(namespaceId: NamespaceId) =
        template.get("/v1/namespaces/{namespaceId}")
            .path("namespaceId", namespaceId)
            .execute()
            .fold(HubNamespace::class)

}