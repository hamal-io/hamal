package io.hamal.request

import io.hamal.lib.domain.vo.EventToSubmit
import io.hamal.lib.domain.vo.ExecResult
import io.hamal.lib.domain.vo.ExecState

interface ExecFailReq {
    val result: ExecResult
}

interface ExecCompleteReq {
    val result: ExecResult
    val state: ExecState
    val events: List<EventToSubmit>
}