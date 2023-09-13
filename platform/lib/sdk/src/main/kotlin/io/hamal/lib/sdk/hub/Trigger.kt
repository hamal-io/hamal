package io.hamal.lib.sdk.hub

import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.fold
import io.hamal.request.CreateTriggerReq
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class HubCreateTriggerReq(
    override val type: TriggerType,
    override val name: TriggerName,
    override val funcId: FuncId,
    override val namespaceId: NamespaceId? = null,
    override val inputs: TriggerInputs,
    override val correlationId: CorrelationId? = null,
    override val duration: Duration? = null,
    override val topicId: TopicId? = null,
) : CreateTriggerReq

@Serializable
data class HubTriggerList(
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
}

@Serializable
sealed interface HubTrigger {
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
class HubFixedRateTrigger(
    override val id: TriggerId,
    override val name: TriggerName,
    override val func: HubTrigger.Func,
    override val namespace: HubTrigger.Namespace,
    override val inputs: TriggerInputs,
    override val correlationId: CorrelationId? = null,
    val duration: Duration
) : HubTrigger

@Serializable
class HubEventTrigger(
    override val id: TriggerId,
    override val name: TriggerName,
    override val func: HubTrigger.Func,
    override val namespace: HubTrigger.Namespace,
    override val inputs: TriggerInputs,
    override val correlationId: CorrelationId? = null,
    val topic: Topic
) : HubTrigger {
    @Serializable
    data class Topic(
        val id: TopicId,
        val name: TopicName
    )
}


interface HubTriggerService {
    fun create(groupId: GroupId, req: HubCreateTriggerReq): HubSubmittedReqWithId
    fun list(groupId: GroupId): List<HubTriggerList.Trigger>
    fun get(triggerId: TriggerId): HubTrigger
}


internal class DefaultHubTriggerService(
    private val template: HttpTemplate
) : HubTriggerService {
    override fun create(groupId: GroupId, req: HubCreateTriggerReq) =
        template.post("/v1/groups/{groupId}/triggers")
            .path("groupId", groupId)
            .body(req)
            .execute()
            .fold(HubSubmittedReqWithId::class)

    override fun list(groupId: GroupId) =
        template.get("/v1/groups/{groupId}/triggers")
            .path("groupId", groupId)
            .execute()
            .fold(HubTriggerList::class)
            .triggers

    override fun get(triggerId: TriggerId) =
        template.get("/v1/triggers/{triggerId}")
            .path("triggerId", triggerId)
            .execute()
            .fold(HubTrigger::class)
}