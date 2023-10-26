package io.hamal.lib.sdk.api

import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.http.body
import io.hamal.lib.sdk.fold
import io.hamal.request.CreateHookReq
import io.hamal.request.UpdateHookReq
import kotlinx.serialization.Serializable

@Serializable
data class ApiCreateHookReq(
    override val name: HookName
) : CreateHookReq

@Serializable
data class ApiUpdateHookReq(
    override val namespaceId: NamespaceId? = null,
    override val name: HookName? = null
) : UpdateHookReq

@Serializable
data class ApiHookList(
    val hooks: List<Hook>
) {
    @Serializable
    data class Hook(
        val id: HookId,
        val namespace: Namespace,
        val name: HookName
    ) {
        @Serializable
        data class Namespace(
            val id: NamespaceId,
            val name: NamespaceName
        )
    }
}


@Serializable
data class ApiHook(
    val id: HookId,
    val namespace: Namespace,
    val name: HookName,
) {
    @Serializable
    data class Namespace(
        val id: NamespaceId,
        val name: NamespaceName
    )

    @Serializable
    data class Code(
        val id: CodeId,
        val version: CodeVersion,
        val value: CodeValue
    )
}

interface ApiHookService {
    fun create(namespaceId: NamespaceId, createHookReq: ApiCreateHookReq): ApiSubmittedReqWithId
    fun list(groupId: GroupId): List<ApiHookList.Hook>
    fun get(hookId: HookId): ApiHook
}

internal class ApiHookServiceImpl(
    private val template: HttpTemplateImpl
) : ApiHookService {

    override fun create(namespaceId: NamespaceId, createHookReq: ApiCreateHookReq) =
        template.post("/v1/namespaces/{namespaceId}/hooks")
            .path("namespaceId", namespaceId)
            .body(createHookReq)
            .execute()
            .fold(ApiSubmittedReqWithId::class)

    override fun list(groupId: GroupId): List<ApiHookList.Hook> =
        template.get("/v1/hooks")
            .parameter("group_ids", groupId)
            .execute()
            .fold(ApiHookList::class)
            .hooks

    override fun get(hookId: HookId) =
        template.get("/v1/hooks/{hookId}")
            .path("hookId", hookId)
            .execute()
            .fold(ApiHook::class)

}