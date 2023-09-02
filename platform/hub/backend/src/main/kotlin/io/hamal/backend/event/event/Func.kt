package io.hamal.backend.event.event

import io.hamal.repository.api.Func
import kotlinx.serialization.Serializable

@Serializable
@HubEventTopic("func::created")
data class FuncCreatedEvent(
    val func: Func,
) : HubEvent()


@Serializable
@HubEventTopic("func::updated")
data class FuncUpdatedEvent(
    val func: Func,
) : HubEvent()

