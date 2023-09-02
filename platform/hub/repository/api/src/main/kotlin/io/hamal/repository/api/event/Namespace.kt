package io.hamal.repository.api.event

import io.hamal.repository.api.Namespace
import kotlinx.serialization.Serializable

@Serializable
@HubEventTopic("namespace::created")
data class NamespaceCreatedEvent(
    val namespace: Namespace,
) : HubEvent()


@Serializable
@HubEventTopic("namespace::updated")
data class NamespaceUpdatedEvent(
    val namespace: Namespace,
) : HubEvent()

