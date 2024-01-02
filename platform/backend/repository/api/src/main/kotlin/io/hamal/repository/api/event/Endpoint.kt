package io.hamal.repository.api.event

import io.hamal.repository.api.Endpoint

@PlatformEventTopic("endpoint::created")
data class EndpointCreatedEvent(
    val endpoint: Endpoint,
) : PlatformEvent()


@PlatformEventTopic("endpoint::updated")
data class EndpointUpdatedEvent(
    val endpoint: Endpoint,
) : PlatformEvent()
