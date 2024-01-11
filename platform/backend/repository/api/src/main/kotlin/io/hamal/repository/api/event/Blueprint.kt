package io.hamal.repository.api.event

import io.hamal.repository.api.Blueprint

@PlatformEventTopic("blueprint::created")
data class BlueprintCreatedEvent(
    val blueprint: Blueprint
) : PlatformEvent()


@PlatformEventTopic("blueprint::updated")
data class BlueprintUpdatedEvent(
    val blueprint: Blueprint,
) : PlatformEvent()

