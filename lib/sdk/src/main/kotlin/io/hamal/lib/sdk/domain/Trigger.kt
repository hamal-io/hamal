package io.hamal.lib.sdk.domain

import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
@Deprecated("do not have separate dto")
data class ApiCreateTriggerRequest(
    val name: TriggerName,
    val funcId: FuncId,
    val type: TriggerType,
    val duration: Duration? = null,
    val topicId: TopicId? = null
)

@Serializable
@Deprecated("do not have separate dto")
data class ApiCreateTriggerResponse(
    val id: TriggerId,
    val name: TriggerName,
)


@Serializable
data class ApiTriggerList(
    val triggers: List<ApiSimpleTrigger>
) {

    @Serializable
    data class ApiSimpleTrigger(
        val id: TriggerId,
        val name: TriggerName
    )
}


@Serializable
sealed interface ApiTrigger {
    val id: TriggerId
    val name: TriggerName
    val func: Func
    val inputs: TriggerInputs
    val correlationId: CorrelationId?

    @Serializable
    data class Func(
        val id: FuncId,
        val name: FuncName
    )
}

@Serializable
class ApiFixedRateTrigger(
    override val id: TriggerId,
    override val name: TriggerName,
    override val func: ApiTrigger.Func,
    override val inputs: TriggerInputs,
    override val correlationId: CorrelationId? = null,
    val duration: Duration
) : ApiTrigger

@Serializable
class ApiEventTrigger(
    override val id: TriggerId,
    override val name: TriggerName,
    override val func: ApiTrigger.Func,
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
