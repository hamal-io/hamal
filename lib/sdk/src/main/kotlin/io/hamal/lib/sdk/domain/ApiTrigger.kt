package io.hamal.lib.sdk.domain

import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.domain.vo.TriggerName
import kotlinx.serialization.Serializable

@Serializable
data class ApiCreateTriggerRequest(
    val name: TriggerName,
    val code: Code
)

@Serializable
data class ApiCreateTriggerResponse(
    val id: TriggerId,
    val name: TriggerName,
    val code: Code
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


