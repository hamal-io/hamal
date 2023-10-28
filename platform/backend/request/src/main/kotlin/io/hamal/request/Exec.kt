package io.hamal.request

import io.hamal.lib.domain.vo.EventToSubmit
import io.hamal.lib.domain.vo.ExecResult
import io.hamal.lib.domain.vo.ExecState

interface FailExecReq {
    val result: ExecResult
}

interface CompleteExecReq {
    val result: ExecResult
    val state: ExecState
    val events: List<EventToSubmit>
}