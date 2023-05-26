package io.hamal.lib.sdk.domain

import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.domain.vo.TriggerName
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class ApiCreateTriggerRequest(
    val name: TriggerName,
    val funcId: FuncId,
    val type: TriggerType,
    val duration: Duration? = null
)

@Serializable
data class ApiCreateTriggerResponse(
    val id: TriggerId,
    val name: TriggerName,
)


@Serializable
data class ApiListTriggerResponse(
    val triggers: List<Trigger>
) {

    @Serializable
    data class Trigger(
        val id: TriggerId,
        val name: TriggerName
    )
}


