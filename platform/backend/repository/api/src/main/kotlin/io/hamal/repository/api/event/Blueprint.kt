package io.hamal.repository.api.event

import io.hamal.repository.api.Blueprint
import kotlinx.serialization.Serializable

@Serializable
@PlatformEventTopic("blueprint::created")
data class BlueprintCreatedEvent(
    val blueprint: Blueprint
) : PlatformEvent()


@Serializable
@PlatformEventTopic("blueprint::updated")
data class BlueprintUpdatedEvent(
    val blueprint: Blueprint,
) : PlatformEvent()

