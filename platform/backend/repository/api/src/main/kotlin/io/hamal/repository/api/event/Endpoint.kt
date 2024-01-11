package io.hamal.repository.api.event

import io.hamal.repository.api.Endpoint

data class EndpointCreatedEvent(
    val endpoint: Endpoint,
) : PlatformEvent()


data class EndpointUpdatedEvent(
    val endpoint: Endpoint,
) : PlatformEvent()
