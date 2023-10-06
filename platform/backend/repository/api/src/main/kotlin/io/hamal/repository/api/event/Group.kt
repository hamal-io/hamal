package io.hamal.repository.api.event

import io.hamal.repository.api.Group
import kotlinx.serialization.Serializable

@Serializable
@PlatformEventTopic("group::created")
data class GroupCreatedEvent(
    val group: Group,
) : PlatformEvent()
