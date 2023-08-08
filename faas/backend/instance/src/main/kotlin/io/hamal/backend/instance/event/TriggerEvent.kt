package io.hamal.backend.instance.event

import io.hamal.lib.domain.Trigger
import kotlinx.serialization.Serializable

@Serializable
@SystemEventTopic("trigger::created")
data class TriggerCreatedEvent(
    val trigger: Trigger
) : SystemEvent()

