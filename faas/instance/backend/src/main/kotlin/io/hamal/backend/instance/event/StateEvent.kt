package io.hamal.backend.instance.event

import io.hamal.lib.domain.CorrelatedState
import kotlinx.serialization.Serializable

@Serializable
@SystemTopic("state::updated")
data class StateUpdatedEvent(
    val state: CorrelatedState,
) : SystemEvent()
