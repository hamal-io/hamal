package io.hamal.backend.instance.event.event

import io.hamal.repository.api.Trigger
import kotlinx.serialization.Serializable

@Serializable
@InstanceEventTopic("trigger::created")
data class TriggerCreatedEvent(
    val trigger: Trigger
) : InstanceEvent()

