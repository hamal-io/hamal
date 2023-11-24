package io.hamal.repository.api.event

import io.hamal.repository.api.Endpoint
import kotlinx.serialization.Serializable

@Serializable
@PlatformEventTopic("endpoint::created")
data class EndpointCreatedEvent(
    val endpoint: Endpoint,
) : PlatformEvent()


@Serializable
@PlatformEventTopic("endpoint::updated")
data class EndpointUpdatedEvent(
    val endpoint: Endpoint,
) : PlatformEvent()
