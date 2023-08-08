package io.hamal.backend.instance.event

import io.hamal.lib.domain.Func
import kotlinx.serialization.Serializable

@Serializable
@SystemTopic("func::created")
data class FuncCreatedEvent(
    val func: Func,
) : SystemEvent()


@Serializable
@SystemTopic("func::updated")
data class FuncUpdatedEvent(
    val func: Func,
) : SystemEvent()

