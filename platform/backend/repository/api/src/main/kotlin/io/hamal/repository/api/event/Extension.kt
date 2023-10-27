package io.hamal.repository.api.event

import io.hamal.repository.api.Extension
import kotlinx.serialization.Serializable

@Serializable
@PlatformEventTopic("extension::created")
data class ExtensionCreatedEvent(
    val extension: Extension,
) : PlatformEvent()

@Serializable
@PlatformEventTopic("extension::updated")
data class ExtensionUpdatedEvent(
    val extension: Extension,
) : PlatformEvent()