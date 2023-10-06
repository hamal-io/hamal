package io.hamal.repository.api.event

import io.hamal.repository.api.Namespace
import kotlinx.serialization.Serializable

@Serializable
@PlatformEventTopic("namespace::created")
data class NamespaceCreatedEvent(
    val namespace: Namespace,
) : PlatformEvent()


@Serializable
@PlatformEventTopic("namespace::updated")
data class NamespaceUpdatedEvent(
    val namespace: Namespace,
) : PlatformEvent()

