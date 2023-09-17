package io.hamal.lib.sdk.admin

import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.fold
import io.hamal.request.CreateTriggerReq
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class AdminCreateTriggerReq(
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
data class AdminTriggerList(
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
sealed interface AdminTrigger {
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
class AdminFixedRateTrigger(
    override val id: TriggerId,
    override val name: TriggerName,
    override val func: AdminTrigger.Func,
    override val namespace: AdminTrigger.Namespace,
    override val inputs: TriggerInputs,
    override val correlationId: CorrelationId? = null,
    val duration: Duration
) : AdminTrigger

@Serializable
class AdminEventTrigger(
    override val id: TriggerId,
    override val name: TriggerName,
    override val func: AdminTrigger.Func,
    override val namespace: AdminTrigger.Namespace,
    override val inputs: TriggerInputs,
    override val correlationId: CorrelationId? = null,
    val topic: Topic
) : AdminTrigger {
    @Serializable
    data class Topic(
        val id: TopicId,
        val name: TopicName
    )
}


interface AdminTriggerService {
    fun create(req: AdminCreateTriggerReq): AdminSubmittedReqWithId
    fun list(groupId: GroupId): List<AdminTriggerList.Trigger>
    fun get(triggerId: TriggerId): AdminTrigger
}


internal class DefaultAdminTriggerService(
    private val template: HttpTemplate
) : AdminTriggerService {
    override fun create(req: AdminCreateTriggerReq) =
        template.post("/v1/triggers")
            .body(req)
            .execute()
            .fold(AdminSubmittedReqWithId::class)

    override fun list(groupId: GroupId) =
        template.get("/v1/groups/{groupId}/triggers")
            .path("groupId", groupId)
            .execute()
            .fold(AdminTriggerList::class)
            .triggers

    override fun get(triggerId: TriggerId) =
        template.get("/v1/triggers/{triggerId}")
            .path("triggerId", triggerId)
            .execute()
            .fold(AdminTrigger::class)
}