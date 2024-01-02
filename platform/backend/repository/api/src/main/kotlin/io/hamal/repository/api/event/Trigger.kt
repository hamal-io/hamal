package io.hamal.repository.api.event

import io.hamal.repository.api.Trigger

@PlatformEventTopic("trigger::created")
data class TriggerCreatedEvent(
    val trigger: Trigger
) : PlatformEvent()

@PlatformEventTopic("trigger::status")
data class TriggerActivatedEvent(
    val trigger: Trigger
) : PlatformEvent()

@PlatformEventTopic("trigger::status")
data class TriggerDeactivatedEvent(
    val trigger: Trigger
) : PlatformEvent()