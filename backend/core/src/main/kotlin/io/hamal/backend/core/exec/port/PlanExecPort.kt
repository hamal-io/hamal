package io.hamal.backend.core.exec.port

import io.hamal.backend.core.exec.PlannedExec
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.FuncId

fun interface PlanExecPort {
    fun planExec(execToPlan: ExecToPlan): PlannedExec
    data class ExecToPlan(
        val execId: ExecId,
        val definitionId: FuncId
    )
}