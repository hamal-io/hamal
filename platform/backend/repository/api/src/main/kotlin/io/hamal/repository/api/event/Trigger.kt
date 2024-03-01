package io.hamal.repository.api.event

import io.hamal.repository.api.Trigger

data class TriggerCreatedEvent(
    val trigger: Trigger
) : InternalEvent()

data class TriggerActivatedEvent(
    val trigger: Trigger
) : InternalEvent()

data class TriggerDeactivatedEvent(
    val trigger: Trigger
) : InternalEvent()