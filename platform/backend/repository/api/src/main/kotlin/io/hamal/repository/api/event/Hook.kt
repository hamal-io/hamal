package io.hamal.repository.api.event

import io.hamal.repository.api.Hook
import kotlinx.serialization.Serializable

@Serializable
@PlatformEventTopic("hook::created")
data class HookCreatedEvent(
    val hook: Hook,
) : PlatformEvent()


@Serializable
@PlatformEventTopic("hook::updated")
data class HookUpdatedEvent(
    val hook: Hook,
) : PlatformEvent()

