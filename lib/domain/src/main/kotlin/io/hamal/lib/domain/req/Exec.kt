package io.hamal.lib.domain.req

import io.hamal.lib.domain.Event
import io.hamal.lib.domain.State
import io.hamal.lib.kua.value.ErrorValue
import kotlinx.serialization.Serializable

@Serializable
data class FailExecReq(
    val cause: ErrorValue
)

@Serializable
data class CompleteExecReq(
    val state: State,
    val events: List<Event>
)

