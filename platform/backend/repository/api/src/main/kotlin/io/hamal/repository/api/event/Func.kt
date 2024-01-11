package io.hamal.repository.api.event

import io.hamal.repository.api.Func

data class FuncCreatedEvent(
    val func: Func,
) : PlatformEvent()


data class FuncUpdatedEvent(
    val func: Func,
) : PlatformEvent()


data class FuncDeployedEvent(
    val func: Func,
) : PlatformEvent()
