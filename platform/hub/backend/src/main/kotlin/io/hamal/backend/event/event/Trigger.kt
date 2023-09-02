package io.hamal.backend.event.event

import io.hamal.repository.api.Trigger
import kotlinx.serialization.Serializable

@Serializable
@HubEventTopic("trigger::created")
data class TriggerCreatedEvent(
    val trigger: Trigger
) : HubEvent()

