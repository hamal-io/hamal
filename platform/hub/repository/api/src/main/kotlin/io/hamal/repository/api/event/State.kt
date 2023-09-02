package io.hamal.repository.api.event

import io.hamal.lib.domain.CorrelatedState
import kotlinx.serialization.Serializable

@Serializable
@HubEventTopic("state::updated")
data class StateUpdatedEvent(
    val state: CorrelatedState,
) : HubEvent()
