package io.hamal.repository.api.event

import io.hamal.repository.api.Func

@PlatformEventTopic("func::created")
data class FuncCreatedEvent(
    val func: Func,
) : PlatformEvent()


@PlatformEventTopic("func::updated")
data class FuncUpdatedEvent(
    val func: Func,
) : PlatformEvent()


@PlatformEventTopic("func::deployed")
data class FuncDeployedEvent(
    val func: Func,
) : PlatformEvent()
