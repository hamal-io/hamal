package io.hamal.backend.instance.event

import io.hamal.repository.api.Namespace
import kotlinx.serialization.Serializable

@Serializable
@SystemTopic("namespace::created")
data class NamespaceCreatedEvent(
    val namespace: Namespace,
) : SystemEvent()


@Serializable
@SystemTopic("namespace::updated")
data class NamespaceUpdatedEvent(
    val namespace: Namespace,
) : SystemEvent()

