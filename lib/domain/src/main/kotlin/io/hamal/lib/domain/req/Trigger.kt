package io.hamal.lib.domain.req

import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class CreateTriggerReq(
    val type: TriggerType,
    val name: TriggerName,
    val funcId: FuncId,
    val inputs: TriggerInputs,
    val correlationId: CorrelationId? = null,
    val duration: Duration? = null,
    val topicId: TopicId? = null,
)

