package io.hamal.repository.api.event

import io.hamal.repository.api.Func

data class FuncCreatedEvent(
    val func: Func,
) : InternalEvent()


data class FuncUpdatedEvent(
    val func: Func,
) : InternalEvent()


data class FuncDeployedEvent(
    val func: Func,
) : InternalEvent()
