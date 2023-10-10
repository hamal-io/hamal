package io.hamal.request

import io.hamal.lib.domain.EventToSubmit
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.ExecResult

interface FailExecReq {
    val result: ExecResult
}

interface CompleteExecReq {
    val state: State
    val result: ExecResult
    val events: List<EventToSubmit>
}