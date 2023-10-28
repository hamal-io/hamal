package io.hamal.lib.sdk.api

import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain._enum.HookMethod
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpRequest
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiTriggerService.TriggerQuery
import io.hamal.lib.sdk.fold
import io.hamal.lib.sdk.foldReq
import io.hamal.request.CreateTriggerReq
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
data class ApiTriggerList(
    val triggers: List<Trigger>
) {
    @Serializable
    sealed interface Trigger {
        val id: TriggerId
        val name: TriggerName
        val func: Func
        val namespace: Namespace

        @Serializable
        data class Func(
            val id: FuncId,
            val name: FuncName
        )

        @Serializable
        data class Namespace(
            val id: NamespaceId,
            val name: NamespaceName
        )
    }

    @Serializable
    class FixedRateTrigger(
        override val id: TriggerId,
        override val name: TriggerName,
        override val func: Trigger.Func,
        override val namespace: Trigger.Namespace,
        val duration: Duration
    ) : Trigger

    @Serializable
    class EventTrigger(
        override val id: TriggerId,
        override val name: TriggerName,
        override val func: Trigger.Func,
        override val namespace: Trigger.Namespace,
        val topic: Topic
    ) : Trigger {
        @Serializable
        data class Topic(
            val id: TopicId,
            val name: TopicName
        )
    }

    @Serializable
    class HookTrigger(
        override val id: TriggerId,
        override val name: TriggerName,
        override val func: Trigger.Func,
        override val namespace: Trigger.Namespace,
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
    val namespace: Namespace
    val inputs: TriggerInputs
    val correlationId: CorrelationId?

    @Serializable
    data class Func(
        val id: FuncId,
        val name: FuncName
    )

    @Serializable
    data class Namespace(
        val id: NamespaceId,
        val name: NamespaceName
    )
}

@Serializable
class ApiFixedRateTrigger(
    override val id: TriggerId,
    override val name: TriggerName,
    override val func: ApiTrigger.Func,
    override val namespace: ApiTrigger.Namespace,
    override val inputs: TriggerInputs,
    override val correlationId: CorrelationId? = null,
    val duration: Duration
) : ApiTrigger

@Serializable
class ApiEventTrigger(
    override val id: TriggerId,
    override val name: TriggerName,
    override val func: ApiTrigger.Func,
    override val namespace: ApiTrigger.Namespace,
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
class ApiHookTrigger(
    override val id: TriggerId,
    override val name: TriggerName,
    override val func: ApiTrigger.Func,
    override val namespace: ApiTrigger.Namespace,
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
    fun create(namespaceId: NamespaceId, req: ApiTriggerCreateReq): ApiSubmittedReqImpl<TriggerId>
    fun list(query: TriggerQuery): List<ApiTriggerList.Trigger>
    fun get(triggerId: TriggerId): ApiTrigger

    data class TriggerQuery(
        var afterId: FuncId = FuncId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(25),
        var funcIds: List<FuncId> = listOf(),
        var namespaceIds: List<NamespaceId> = listOf(),
        var groupIds: List<GroupId> = listOf()
    ) {
        fun setRequestParameters(req: HttpRequest) {
            req.parameter("after_id", afterId)
            req.parameter("limit", limit)
            if (funcIds.isNotEmpty()) req.parameter("func_ids", funcIds)
            if (namespaceIds.isNotEmpty()) req.parameter("namespace_ids", namespaceIds)
            if (groupIds.isNotEmpty()) req.parameter("group_ids", groupIds)
        }
    }
}


internal class ApiTriggerServiceImpl(
    private val template: HttpTemplateImpl
) : ApiTriggerService {
    override fun create(namespaceId: NamespaceId, req: ApiTriggerCreateReq): ApiSubmittedReqImpl<TriggerId> =
        template.post("/v1/namespaces/{namespaceId}/triggers")
            .path("namespaceId", namespaceId)
            .body(req)
            .execute()
            .foldReq()

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