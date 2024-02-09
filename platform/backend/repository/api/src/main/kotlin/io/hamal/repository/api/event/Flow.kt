package io.hamal.repository.api.event

import io.hamal.repository.api.Flow

data class FlowCreatedEvent(
    val flow: Flow,
) : InternalEvent()


data class FlowUpdatedEvent(
    val flow: Flow,
) : InternalEvent()

