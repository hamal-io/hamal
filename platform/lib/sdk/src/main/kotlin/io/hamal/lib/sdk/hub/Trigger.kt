package io.hamal.lib.sdk.hub

import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable
import kotlin.time.Duration

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


