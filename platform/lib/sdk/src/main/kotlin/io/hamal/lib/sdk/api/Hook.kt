package io.hamal.lib.sdk.api

import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpRequest
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiHookService.HookQuery
import io.hamal.lib.sdk.fold
import io.hamal.request.HookCreateReq
import io.hamal.request.HookUpdateReq

data class ApiHookCreateReq(
    override val name: HookName
) : HookCreateReq

data class ApiHookCreateSubmitted(
    override val id: ReqId,
    override val status: ReqStatus,
    val hookId: HookId,
    val groupId: GroupId,
    val flowId: FlowId
) : ApiSubmitted

data class ApiHookUpdateReq(
    override val name: HookName? = null
) : HookUpdateReq

data class ApiHookUpdateSubmitted(
    override val id: ReqId,
    override val status: ReqStatus,
    val hookId: HookId,
) : ApiSubmitted

data class ApiHookList(
    val hooks: List<Hook>
) {
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
) {
    data class Flow(
        val id: FlowId,
        val name: FlowName
    )
}

interface ApiHookService {
    fun create(flowId: FlowId, createHookReq: ApiHookCreateReq): ApiHookCreateSubmitted
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

    override fun create(flowId: FlowId, createHookReq: ApiHookCreateReq) =
        template.post("/v1/flows/{flowId}/hooks")
            .path("flowId", flowId)
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