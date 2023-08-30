package io.hamal.backend.instance.event.events

import io.hamal.repository.api.Func
import kotlinx.serialization.Serializable

@Serializable
@InstanceEventTopic("func::created")
data class FuncCreatedEvent(
    val func: Func,
) : InstanceEvent()


@Serializable
@InstanceEventTopic("func::updated")
data class FuncUpdatedEvent(
    val func: Func,
) : InstanceEvent()

