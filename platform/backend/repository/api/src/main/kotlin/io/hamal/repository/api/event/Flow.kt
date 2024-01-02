package io.hamal.repository.api.event

import io.hamal.repository.api.Flow

@PlatformEventTopic("flow::created")
data class FlowCreatedEvent(
    val flow: Flow,
) : PlatformEvent()


@PlatformEventTopic("flow::updated")
data class FlowUpdatedEvent(
    val flow: Flow,
) : PlatformEvent()

