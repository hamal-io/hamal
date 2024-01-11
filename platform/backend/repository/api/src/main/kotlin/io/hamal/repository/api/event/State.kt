package io.hamal.repository.api.event

import io.hamal.lib.domain.CorrelatedState

data class StateUpdatedEvent(
    val state: CorrelatedState,
) : PlatformEvent()
