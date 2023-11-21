package io.hamal.repository.api.event

import io.hamal.repository.api.Trigger
import kotlinx.serialization.Serializable

@Serializable
@PlatformEventTopic("trigger::created")
data class TriggerCreatedEvent(
    val trigger: Trigger
) : PlatformEvent()

@Serializable
@PlatformEventTopic("trigger::status")
data class TriggerStatusEvent(
    val trigger: Trigger
) : PlatformEvent()
