package io.hamal.backend.event

import io.hamal.backend.repository.api.domain.Trigger
import kotlinx.serialization.Serializable

@Serializable
@SystemEventTopic("trigger::created")
data class TriggerCreatedEvent(
    val trigger: Trigger
) : SystemEvent()
