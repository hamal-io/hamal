package io.hamal.backend.instance.req.handler.exec

import io.hamal.backend.instance.event.ExecPlannedEvent
import io.hamal.backend.instance.event.SystemEventEmitter
import io.hamal.backend.instance.req.ReqHandler
import io.hamal.backend.instance.req.handler.cmdId
import io.hamal.backend.repository.api.ExecCmdRepository
import io.hamal.backend.repository.api.FuncQueryRepository
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.PlannedExec
import io.hamal.lib.domain.req.SubmittedInvokeOneshotReq
import org.springframework.stereotype.Component

@Component
class InvokeOneshotHandler(
    private val execCmdRepository: ExecCmdRepository,
    private val eventEmitter: SystemEventEmitter<*>,
    private val funcQueryRepository: FuncQueryRepository
) : ReqHandler<SubmittedInvokeOneshotReq>(SubmittedInvokeOneshotReq::class) {
    override fun invoke(req: SubmittedInvokeOneshotReq) {
        planExec(req).also { emitEvent(req.cmdId(), it) }
    }

    private fun planExec(req: SubmittedInvokeOneshotReq): PlannedExec {
        val func = funcQueryRepository.get(req.funcId)
        return execCmdRepository.plan(
            ExecCmdRepository.PlanCmd(
                id = req.cmdId(),
                execId = req.execId,
                correlation = Correlation(
                    correlationId = req.correlationId,
                    funcId = func.id
                ),
                inputs = merge(func.inputs, req.inputs),
                code = func.code,
            )
        )
    }

    private fun emitEvent(cmdId: CmdId, exec: PlannedExec) {
        eventEmitter.emit(cmdId, ExecPlannedEvent(exec))
    }
}