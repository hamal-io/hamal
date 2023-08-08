package io.hamal.backend.instance.event

import io.hamal.lib.domain.Trigger
import kotlinx.serialization.Serializable

@Serializable
@SystemTopic("trigger::created")
data class TriggerCreatedEvent(
    val trigger: Trigger
) : SystemEvent()

