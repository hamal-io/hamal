package io.hamal.repository.api.event

import io.hamal.repository.api.Extension

@PlatformEventTopic("extension::created")
data class ExtensionCreatedEvent(
    val extension: Extension,
) : PlatformEvent()

@PlatformEventTopic("extension::updated")
data class ExtensionUpdatedEvent(
    val extension: Extension,
) : PlatformEvent()