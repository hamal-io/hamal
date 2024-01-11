package io.hamal.repository.api.event

import io.hamal.repository.api.Group

data class GroupCreatedEvent(
    val group: Group,
) : PlatformEvent()
