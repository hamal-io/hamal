package io.hamal.repository.api.event

import io.hamal.repository.api.Extension

data class ExtensionCreatedEvent(
    val extension: Extension,
) : InternalEvent()

data class ExtensionUpdatedEvent(
    val extension: Extension,
) : InternalEvent()