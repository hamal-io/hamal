package io.hamal.backend.instance.event

import io.hamal.lib.domain.Func
import kotlinx.serialization.Serializable

@Serializable
@SystemEventTopic("func::created")
data class FuncCreatedEvent(
    val func: Func,
) : SystemEvent()


@Serializable
@SystemEventTopic("func::updated")
data class FuncUpdatedEvent(
    val func: Func,
) : SystemEvent()

