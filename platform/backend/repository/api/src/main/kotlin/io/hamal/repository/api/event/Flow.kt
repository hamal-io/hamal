package io.hamal.repository.api.event

import io.hamal.repository.api.Flow

data class FlowCreatedEvent(
    val flow: Flow,
) : PlatformEvent()


data class FlowUpdatedEvent(
    val flow: Flow,
) : PlatformEvent()

