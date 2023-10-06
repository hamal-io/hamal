package io.hamal.repository.api.event

import io.hamal.repository.api.Func
import kotlinx.serialization.Serializable

@Serializable
@PlatformEventTopic("func::created")
data class FuncCreatedEvent(
    val func: Func,
) : PlatformEvent()


@Serializable
@PlatformEventTopic("func::updated")
data class FuncUpdatedEvent(
    val func: Func,
) : PlatformEvent()

