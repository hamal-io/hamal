package io.hamal.repository.api.event

import io.hamal.repository.api.Endpoint

data class EndpointCreatedEvent(
    val endpoint: Endpoint,
) : InternalEvent()


data class EndpointUpdatedEvent(
    val endpoint: Endpoint,
) : InternalEvent()
