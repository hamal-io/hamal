package io.hamal.repository.api.event

import io.hamal.repository.api.Group

@PlatformEventTopic("group::created")
data class GroupCreatedEvent(
    val group: Group,
) : PlatformEvent()
