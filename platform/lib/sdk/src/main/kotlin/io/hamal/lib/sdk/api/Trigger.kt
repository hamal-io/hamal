package io.hamal.lib.sdk.api

import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain._enum.HookMethod
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpRequest
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiTriggerService.TriggerQuery
import io.hamal.lib.sdk.fold
import io.hamal.request.CreateTriggerReq
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class ApiTriggerCreateReq(
    override val type: TriggerType,
    override val name: TriggerName,
    override val funcId: FuncId,
    override val inputs: TriggerInputs,
    override val correlationId: CorrelationId? = null,
    override val duration: Duration? = null,
    override val topicId: TopicId? = null,
    override val hookId: HookId? = null,
    override val hookMethods: Set<HookMethod>? = null
) : CreateTriggerReq

@Serializable
data class ApiTriggerCreateSubmitted(
    override val id: ReqId,
    override val status: ReqStatus,
    val triggerId: TriggerId,
    val groupId: GroupId,
    val flowId: FlowId
) : ApiSubmitted


@Serializable
data class ApiTriggerList(
    val triggers: List<Trigger>
) {
    @Serializable
    sealed interface Trigger {
        val id: TriggerId
        val name: TriggerName
        val func: Func
        val flow: Flow

        @Serializable
        data class Func(
            val id: FuncId,
            val name: FuncName
        )

        @Serializable
        data class Flow(
            val id: FlowId,
            val name: FlowName
        )
    }

    @Serializable
    @SerialName("FixedRate")
    class FixedRateTrigger(
        override val id: TriggerId,
        override val name: TriggerName,
        override val func: Trigger.Func,
        override val flow: Trigger.Flow,
        val duration: Duration
    ) : Trigger

    @Serializable
    @SerialName("Event")
    class EventTrigger(
        override val id: TriggerId,
        override val name: TriggerName,
        override val func: Trigger.Func,
        override val flow: Trigger.Flow,
        val topic: Topic
    ) : Trigger {
        @Serializable
        data class Topic(
            val id: TopicId,
            val name: TopicName
        )
    }

    @Serializable
    @SerialName("Hook")
    class HookTrigger(
        override val id: TriggerId,
        override val name: TriggerName,
        override val func: Trigger.Func,
        override val flow: Trigger.Flow,
        val hook: Hook
    ) : Trigger {
        @Serializable
        data class Hook(
            val id: HookId,
            val name: HookName,
            val methods: Set<HookMethod>
        )
    }
}

@Serializable
sealed interface ApiTrigger {
    val id: TriggerId
    val name: TriggerName
    val func: Func
    val flow: Flow
    val inputs: TriggerInputs
    val correlationId: CorrelationId?

    @Serializable
    data class Func(
        val id: FuncId,
        val name: FuncName
    )

    @Serializable
    data class Flow(
        val id: FlowId,
        val name: FlowName
    )
}

@Serializable
@SerialName("FixedRate")
class ApiFixedRateTrigger(
    override val id: TriggerId,
    override val name: TriggerName,
    override val func: ApiTrigger.Func,
    override val flow: ApiTrigger.Flow,
    override val inputs: TriggerInputs,
    override val correlationId: CorrelationId? = null,
    val duration: Duration
) : ApiTrigger

@Serializable
@SerialName("Event")
class ApiEventTrigger(
    override val id: TriggerId,
    override val name: TriggerName,
    override val func: ApiTrigger.Func,
    override val flow: ApiTrigger.Flow,
    override val inputs: TriggerInputs,
    override val correlationId: CorrelationId? = null,
    val topic: Topic
) : ApiTrigger {
    @Serializable
    data class Topic(
        val id: TopicId,
        val name: TopicName
    )
}

@Serializable
@SerialName("Hook")
class ApiHookTrigger(
    override val id: TriggerId,
    override val name: TriggerName,
    override val func: ApiTrigger.Func,
    override val flow: ApiTrigger.Flow,
    override val inputs: TriggerInputs,
    override val correlationId: CorrelationId? = null,
    val hook: Hook
) : ApiTrigger {
    @Serializable
    data class Hook(
        val id: HookId,
        val name: HookName,
        val methods: Set<HookMethod>
    )
}


interface ApiTriggerService {
    fun create(flowId: FlowId, req: ApiTriggerCreateReq): ApiTriggerCreateSubmitted
    fun list(query: TriggerQuery): List<ApiTriggerList.Trigger>
    fun get(triggerId: TriggerId): ApiTrigger

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
}