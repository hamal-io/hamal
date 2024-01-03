package io.hamal.lib.sdk.api

import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain._enum.HookMethod
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain._enum.TriggerStatus
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.request.TriggerCreateRequest
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpRequest
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiTriggerService.TriggerQuery
import io.hamal.lib.sdk.fold
import kotlin.time.Duration

data class ApiTriggerCreateReq(
    override val type: TriggerType,
    override val name: TriggerName,
    override val funcId: FuncId,
    override val inputs: TriggerInputs,
    override val correlationId: CorrelationId? = null,
    override val duration: Duration? = null,
    override val topicId: TopicId? = null,
    override val hookId: HookId? = null,
    override val hookMethod: HookMethod? = null,
    override val cron: CronPattern? = null
) : TriggerCreateRequest

data class ApiTriggerCreateSubmitted(
    override val id: RequestId,
    override val status: RequestStatus,
    val triggerId: TriggerId,
    val groupId: GroupId,
    val flowId: FlowId
) : ApiRequested


data class ApiTriggerStatusSubmitted(
    override val id: RequestId,
    override val status: RequestStatus,
    val triggerId: TriggerId,
    val triggerStatus: TriggerStatus
) : ApiRequested

data class ApiTriggerList(
    val triggers: List<Trigger>
) {
    sealed interface Trigger {
        val id: TriggerId
        val name: TriggerName
        val func: Func
        val flow: Flow

        data class Func(
            val id: FuncId,
            val name: FuncName
        )

        data class Flow(
            val id: FlowId,
            val name: FlowName
        )
    }

    class FixedRateTrigger(
        override val id: TriggerId,
        override val name: TriggerName,
        override val func: Trigger.Func,
        override val flow: Trigger.Flow,
        val duration: Duration
    ) : Trigger

    class EventTrigger(
        override val id: TriggerId,
        override val name: TriggerName,
        override val func: Trigger.Func,
        override val flow: Trigger.Flow,
        val topic: Topic
    ) : Trigger {
        data class Topic(
            val id: TopicId,
            val name: TopicName
        )
    }

    class HookTrigger(
        override val id: TriggerId,
        override val name: TriggerName,
        override val func: Trigger.Func,
        override val flow: Trigger.Flow,
        val hook: Hook
    ) : Trigger {
        data class Hook(
            val id: HookId,
            val name: HookName,
            val method: HookMethod
        )
    }

    class CronTrigger(
        override val id: TriggerId,
        override val name: TriggerName,
        override val func: Trigger.Func,
        override val flow: Trigger.Flow,
        val cron: CronPattern
    ) : Trigger
}

sealed interface ApiTrigger {
    val id: TriggerId
    val name: TriggerName
    val func: Func
    val flow: Flow
    val inputs: TriggerInputs
    val correlationId: CorrelationId?
    val status: TriggerStatus

    data class Func(
        val id: FuncId,
        val name: FuncName
    )

    data class Flow(
        val id: FlowId,
        val name: FlowName
    )
}

class ApiFixedRateTrigger(
    override val id: TriggerId,
    override val name: TriggerName,
    override val func: ApiTrigger.Func,
    override val flow: ApiTrigger.Flow,
    override val inputs: TriggerInputs,
    override val status: TriggerStatus,
    override val correlationId: CorrelationId? = null,
    val duration: Duration
) : ApiTrigger

class ApiEventTrigger(
    override val id: TriggerId,
    override val name: TriggerName,
    override val func: ApiTrigger.Func,
    override val flow: ApiTrigger.Flow,
    override val inputs: TriggerInputs,
    override val status: TriggerStatus,
    override val correlationId: CorrelationId? = null,
    val topic: Topic
) : ApiTrigger {
    data class Topic(
        val id: TopicId,
        val name: TopicName
    )
}

class ApiHookTrigger(
    override val id: TriggerId,
    override val name: TriggerName,
    override val func: ApiTrigger.Func,
    override val flow: ApiTrigger.Flow,
    override val inputs: TriggerInputs,
    override val status: TriggerStatus,
    override val correlationId: CorrelationId? = null,
    val hook: Hook
) : ApiTrigger {
    data class Hook(
        val id: HookId,
        val name: HookName,
        val method: HookMethod
    )
}


class ApiCronTrigger(
    override val id: TriggerId,
    override val name: TriggerName,
    override val func: ApiTrigger.Func,
    override val flow: ApiTrigger.Flow,
    override val inputs: TriggerInputs,
    override val status: TriggerStatus,
    override val correlationId: CorrelationId? = null,
    val cron: CronPattern
) : ApiTrigger


interface ApiTriggerService {
    fun create(flowId: FlowId, req: ApiTriggerCreateReq): ApiTriggerCreateSubmitted
    fun list(query: TriggerQuery): List<ApiTriggerList.Trigger>
    fun get(triggerId: TriggerId): ApiTrigger
    fun activate(triggerId: TriggerId): ApiTriggerStatusSubmitted
    fun deactivate(triggerId: TriggerId): ApiTriggerStatusSubmitted

    data class TriggerQuery(
        var afterId: FuncId = FuncId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(25),
        var funcIds: List<FuncId> = listOf(),
        var flowIds: List<FlowId> = listOf(),
        var groupIds: List<GroupId> = listOf()
    ) {
        fun setRequestParameters(req: HttpRequest) {
            req.parameter("after_id", afterId)
            req.parameter("limit", limit)
            if (funcIds.isNotEmpty()) req.parameter("func_ids", funcIds)
            if (flowIds.isNotEmpty()) req.parameter("flow_ids", flowIds)
            if (groupIds.isNotEmpty()) req.parameter("group_ids", groupIds)
        }
    }
}


internal class ApiTriggerServiceImpl(
    private val template: HttpTemplate
) : ApiTriggerService {
    override fun create(flowId: FlowId, req: ApiTriggerCreateReq): ApiTriggerCreateSubmitted =
        template.post("/v1/flows/{flowId}/triggers")
            .path("flowId", flowId)
            .body(req)
            .execute()
            .fold(ApiTriggerCreateSubmitted::class)

    override fun list(query: TriggerQuery) =
        template.get("/v1/triggers")
            .also(query::setRequestParameters)
            .execute()
            .fold(ApiTriggerList::class)
            .triggers

    override fun get(triggerId: TriggerId) =
        template.get("/v1/triggers/{triggerId}")
            .path("triggerId", triggerId)
            .execute()
            .fold(ApiTrigger::class)

    override fun activate(triggerId: TriggerId): ApiTriggerStatusSubmitted =
        template.post("/v1/trigger/{triggerId}/activate")
            .path("triggerId", triggerId)
            .execute()
            .fold(ApiTriggerStatusSubmitted::class)


    override fun deactivate(triggerId: TriggerId): ApiTriggerStatusSubmitted =
        template.post("/v1/trigger/{triggerId}/deactivate")
            .path("triggerId", triggerId)
            .execute()
            .fold(ApiTriggerStatusSubmitted::class)
}