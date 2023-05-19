package io.hamal.backend.core.exec.port

import io.hamal.backend.core.exec.CompleteExec
import io.hamal.lib.domain.vo.ExecId

fun interface CompleteExecPort {
    fun completeExec(execId: ExecId): CompleteExec
}