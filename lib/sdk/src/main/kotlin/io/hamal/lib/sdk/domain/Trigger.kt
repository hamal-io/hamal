package io.hamal.lib.sdk.domain

import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class ApiTriggerList(
    val triggers: List<ApiSimpleTrigger>
)

@Serializable
sealed interface ApiSimpleTrigger {
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
class ApiSimpleFixedRateTrigger(
    override val id: TriggerId,
    override val name: TriggerName,
    override val func: ApiSimpleTrigger.Func,
    override val namespace: ApiSimpleTrigger.Namespace,
    val duration: Duration
) : ApiSimpleTrigger

@Serializable
class ApiSimpleEventTrigger(
    override val id: TriggerId,
    override val name: TriggerName,
    override val func: ApiSimpleTrigger.Func,
    override val namespace: ApiSimpleTrigger.Namespace,
    val topic: Topic
) : ApiSimpleTrigger {
    @Serializable
    data class Topic(
        val id: TopicId,
        val name: TopicName
    )
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
