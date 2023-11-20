package io.hamal.repository.api.event

import io.hamal.repository.api.Flow
import kotlinx.serialization.Serializable

@Serializable
@PlatformEventTopic("flow::created")
data class FlowCreatedEvent(
    val flow: Flow,
) : PlatformEvent()


@Serializable
@PlatformEventTopic("flow::updated")
data class FlowUpdatedEvent(
    val flow: Flow,
) : PlatformEvent()

