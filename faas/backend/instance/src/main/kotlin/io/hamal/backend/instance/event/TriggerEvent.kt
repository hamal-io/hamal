package io.hamal.backend.instance.event

import io.hamal.backend.repository.api.Trigger
import kotlinx.serialization.Serializable

@Serializable
@SystemTopic("trigger::created")
data class TriggerCreatedEvent(
    val trigger: Trigger
) : SystemEvent()

