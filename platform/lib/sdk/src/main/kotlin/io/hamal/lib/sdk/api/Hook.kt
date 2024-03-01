package io.hamal.lib.sdk.api

import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.HookCreateRequest
import io.hamal.lib.domain.request.HookUpdateRequest
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpRequest
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiHookService.HookQuery
import io.hamal.lib.sdk.fold

data class ApiHookCreateRequest(
    override val name: HookName
) : HookCreateRequest

data class ApiHookCreateRequested(
    override val id: RequestId,
    override val status: RequestStatus,
    val hookId: HookId,
    val workspaceId: WorkspaceId,
    val namespaceId: NamespaceId
) : ApiRequested()

data class ApiHookUpdateRequest(
    override val name: HookName? = null
) : HookUpdateRequest

data class ApiHookUpdateRequested(
    override val id: RequestId,
    override val status: RequestStatus,
    val hookId: HookId,
) : ApiRequested()

data class ApiHookList(
    val hooks: List<Hook>
) : ApiObject() {
    data class Hook(
        val id: HookId,
        val namespace: Namespace,
        val name: HookName
    ) {
        data class Namespace(
            val id: NamespaceId,
            val name: NamespaceName
        )
    }
}

data class ApiHook(
    val id: HookId,
    val namespace: Namespace,
    val name: HookName,
) : ApiObject() {
    data class Namespace(
        val id: NamespaceId,
        val name: NamespaceName
    )
}

interface ApiHookService {
    fun create(namespaceId: NamespaceId, createHookReq: ApiHookCreateRequest): ApiHookCreateRequested
    fun list(query: HookQuery): List<ApiHookList.Hook>
    fun get(hookId: HookId): ApiHook

    data class HookQuery(
        var afterId: HookId = HookId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(25),
        var hookIds: List<HookId> = listOf(),
        var namespaceIds: List<NamespaceId> = listOf(),
        var workspaceIds: List<WorkspaceId> = listOf()
    ) {
        fun setRequestParameters(req: HttpRequest) {
            req.parameter("after_id", afterId)
            req.parameter("limit", limit)
            if (hookIds.isNotEmpty()) req.parameter("hook_ids", hookIds)
            if (namespaceIds.isNotEmpty()) req.parameter("namespace_ids", namespaceIds)
            if (workspaceIds.isNotEmpty()) req.parameter("workspace_ids", workspaceIds)
        }
    }
}

internal class ApiHookServiceImpl(
    private val template: HttpTemplate
) : ApiHookService {

    override fun create(namespaceId: NamespaceId, createHookReq: ApiHookCreateRequest) =
        template.post("/v1/namespaces/{namespaceId}/hooks")
            .path("namespaceId", namespaceId)
            .body(createHookReq)
            .execute(ApiHookCreateRequested::class)

    override fun list(query: HookQuery): List<ApiHookList.Hook> =
        template.get("/v1/hooks")
            .also(query::setRequestParameters)
            .execute()
            .fold(ApiHookList::class)
            .hooks

    override fun get(hookId: HookId) =
        template.get("/v1/hooks/{hookId}")
            .path("hookId", hookId)
            .execute()
            .fold(ApiHook::class)
}