package io.hamal.repository.api.event

import io.hamal.repository.api.Auth

data class AuthUpdatedEvent(
    val auth: Auth
) : InternalEvent()