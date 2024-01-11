package io.hamal.repository.api.event

import io.hamal.repository.api.Hook

@PlatformEventTopic("hook::created")
data class HookCreatedEvent(
    val hook: Hook,
) : PlatformEvent()


@PlatformEventTopic("hook::updated")
data class HookUpdatedEvent(
    val hook: Hook,
) : PlatformEvent()
