package io.hamal.repository.api.event

import io.hamal.repository.api.Auth

data class AuthUpdateEvent(
    val auth: Auth
) : InternalEvent()