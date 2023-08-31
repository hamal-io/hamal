package io.hamal.backend.instance.event.event

import io.hamal.repository.api.Namespace
import kotlinx.serialization.Serializable

@Serializable
@InstanceEventTopic("namespace::created")
data class NamespaceCreatedEvent(
    val namespace: Namespace,
) : InstanceEvent()


@Serializable
@InstanceEventTopic("namespace::updated")
data class NamespaceUpdatedEvent(
    val namespace: Namespace,
) : InstanceEvent()

