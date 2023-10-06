package io.hamal.request

import io.hamal.lib.domain.EventToSubmit
import io.hamal.lib.domain.State
import io.hamal.lib.kua.type.ErrorType

interface FailExecReq {
    val cause: ErrorType
}

interface CompleteExecReq {
    val state: State
    val events: List<EventToSubmit>
}