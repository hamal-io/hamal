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
data class TriggerActivatedEvent(
    val trigger: Trigger
) : PlatformEvent()

@Serializable
@PlatformEventTopic("trigger::status")
data class TriggerDeactivatedEvent(
    val trigger: Trigger
) : PlatformEvent()