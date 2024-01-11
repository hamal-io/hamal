package io.hamal.repository.api.event

import io.hamal.repository.api.Trigger

data class TriggerCreatedEvent(
    val trigger: Trigger
) : PlatformEvent()

data class TriggerActivatedEvent(
    val trigger: Trigger
) : PlatformEvent()

data class TriggerDeactivatedEvent(
    val trigger: Trigger
) : PlatformEvent()