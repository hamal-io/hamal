package io.hamal.lib.sdk.api

import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpRequest
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiHookService.HookQuery
import io.hamal.lib.sdk.fold
import io.hamal.request.CreateHookReq
import io.hamal.request.UpdateHookReq
import kotlinx.serialization.Serializable

@Serializable
data class ApiHookCreateReq(
    override val name: HookName
) : CreateHookReq

@Serializable
data class ApiHookCreateSubmitted(
    override val id: ReqId,
    override val status: ReqStatus,
    val hookId: HookId,
    val groupId: GroupId,
    val namespaceId: NamespaceId
) : ApiSubmitted


@Serializable
data class ApiUpdateHookReq(
    override val name: HookName? = null
) : UpdateHookReq

@Serializable
data class ApiHookUpdateSubmitted(
    override val id: ReqId,
    override val status: ReqStatus,
    val hookId: HookId,
) : ApiSubmitted

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
    fun create(namespaceId: NamespaceId, createHookReq: ApiHookCreateReq): ApiHookCreateSubmitted
    fun list(query: HookQuery): List<ApiHookList.Hook>
    fun get(hookId: HookId): ApiHook

    data class HookQuery(
        var afterId: HookId = HookId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(25),
        var hookIds: List<HookId> = listOf(),
        var namespaceIds: List<NamespaceId> = listOf(),
        var groupIds: List<GroupId> = listOf()
    ) {
        fun setRequestParameters(req: HttpRequest) {
            req.parameter("after_id", afterId)
            req.parameter("limit", limit)
            if (hookIds.isNotEmpty()) req.parameter("hook_ids", hookIds)
            if (namespaceIds.isNotEmpty()) req.parameter("namespace_ids", namespaceIds)
            if (groupIds.isNotEmpty()) req.parameter("group_ids", groupIds)
        }
    }
}

internal class ApiHookServiceImpl(
    private val template: HttpTemplateImpl
) : ApiHookService {

    override fun create(namespaceId: NamespaceId, createHookReq: ApiHookCreateReq) =
        template.post("/v1/namespaces/{namespaceId}/hooks")
            .path("namespaceId", namespaceId)
            .body(createHookReq)
            .execute(ApiHookCreateSubmitted::class)

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