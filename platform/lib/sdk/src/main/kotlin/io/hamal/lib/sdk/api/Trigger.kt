package io.hamal.lib.sdk.api

import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.fold
import io.hamal.request.CreateTriggerReq
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class ApiCreateTriggerReq(
    override val type: TriggerType,
    override val name: TriggerName,
    override val funcId: FuncId,
    override val namespaceId: NamespaceId? = null,
    override val inputs: TriggerInputs,
    override val correlationId: CorrelationId? = null,
    override val duration: Duration? = null,
    override val topicId: TopicId? = null,
    override val hookId: HookId? = null
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
            val name: HookName
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
        val name: HookName
    )
}


interface ApiTriggerService {
    fun create(groupId: GroupId, req: ApiCreateTriggerReq): ApiSubmittedReqWithId
    fun list(groupId: GroupId): List<ApiTriggerList.Trigger>
    fun get(triggerId: TriggerId): ApiTrigger
}


internal class ApiTriggerServiceImpl(
    private val template: HttpTemplate
) : ApiTriggerService {
    override fun create(groupId: GroupId, req: ApiCreateTriggerReq) =
        template.post("/v1/triggers")
            .path("groupId", groupId)
            .body(req)
            .execute()
            .fold(ApiSubmittedReqWithId::class)

    override fun list(groupId: GroupId) =
        template.get("/v1/groups/{groupId}/triggers")
            .path("groupId", groupId)
            .execute()
            .fold(ApiTriggerList::class)
            .triggers

    override fun get(triggerId: TriggerId) =
        template.get("/v1/triggers/{triggerId}")
            .path("triggerId", triggerId)
            .execute()
            .fold(ApiTrigger::class)
}