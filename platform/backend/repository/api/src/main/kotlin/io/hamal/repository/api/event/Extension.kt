package io.hamal.repository.api.event

import io.hamal.repository.api.Extension

data class ExtensionCreatedEvent(
    val extension: Extension,
) : PlatformEvent()

data class ExtensionUpdatedEvent(
    val extension: Extension,
) : PlatformEvent()