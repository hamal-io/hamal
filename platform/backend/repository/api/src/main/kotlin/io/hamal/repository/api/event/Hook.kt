package io.hamal.repository.api.event

import io.hamal.repository.api.Hook

data class HookCreatedEvent(
    val hook: Hook,
) : InternalEvent()


data class HookUpdatedEvent(
    val hook: Hook,
) : InternalEvent()
