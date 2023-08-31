package io.hamal.backend.instance.event.event

import io.hamal.lib.domain.CorrelatedState
import kotlinx.serialization.Serializable

@Serializable
@InstanceEventTopic("state::updated")
data class StateUpdatedEvent(
    val state: CorrelatedState,
) : InstanceEvent()
