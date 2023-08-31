package io.hamal.lib.domain.req

import io.hamal.lib.domain.EventPayload
import io.hamal.lib.domain.State
import io.hamal.lib.kua.type.ErrorType
import kotlinx.serialization.Serializable

@Serializable
data class FailExecReq(
    val cause: ErrorType
)

@Serializable
data class CompleteExecReq(
    val state: State,
    val events: List<EventPayload>
)

