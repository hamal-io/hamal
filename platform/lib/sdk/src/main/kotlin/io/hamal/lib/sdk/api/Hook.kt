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
    val groupId: GroupId,
    val flowId: FlowId
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
        val flow: Flow,
        val name: HookName
    ) {
        data class Flow(
            val id: FlowId,
            val name: FlowName
        )
    }
}

data class ApiHook(
    val id: HookId,
    val flow: Flow,
    val name: HookName,
) : ApiObject() {
    data class Flow(
        val id: FlowId,
        val name: FlowName
    )
}

interface ApiHookService {
    fun create(flowId: FlowId, createHookReq: ApiHookCreateRequest): ApiHookCreateRequested
    fun list(query: HookQuery): List<ApiHookList.Hook>
    fun get(hookId: HookId): ApiHook

    data class HookQuery(
        var afterId: HookId = HookId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(25),
        var hookIds: List<HookId> = listOf(),
        var flowIds: List<FlowId> = listOf(),
        var groupIds: List<GroupId> = listOf()
    ) {
        fun setRequestParameters(req: HttpRequest) {
            req.parameter("after_id", afterId)
            req.parameter("limit", limit)
            if (hookIds.isNotEmpty()) req.parameter("hook_ids", hookIds)
            if (flowIds.isNotEmpty()) req.parameter("flow_ids", flowIds)
            if (groupIds.isNotEmpty()) req.parameter("group_ids", groupIds)
        }
    }
}

internal class ApiHookServiceImpl(
    private val template: HttpTemplate
) : ApiHookService {

    override fun create(flowId: FlowId, createHookReq: ApiHookCreateRequest) =
        template.post("/v1/flows/{flowId}/hooks")
            .path("flowId", flowId)
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