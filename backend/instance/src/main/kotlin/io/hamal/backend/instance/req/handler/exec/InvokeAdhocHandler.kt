package io.hamal.backend.instance.req.handler.exec

import io.hamal.backend.instance.event.ExecPlannedEvent
import io.hamal.backend.instance.event.SystemEventEmitter
import io.hamal.backend.instance.req.ReqHandler
import io.hamal.backend.instance.req.handler.cmdId
import io.hamal.backend.repository.api.ExecCmdRepository
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.PlannedExec
import io.hamal.lib.domain.req.SubmittedInvokeAdhocReq
import org.springframework.stereotype.Component

@Component
class InvokeAdhocHandler(
    private val execCmdRepository: ExecCmdRepository,
    private val eventEmitter: SystemEventEmitter<*>
) : ReqHandler<SubmittedInvokeAdhocReq>(SubmittedInvokeAdhocReq::class) {
    override fun invoke(req: SubmittedInvokeAdhocReq) {
        planExec(req).also { emitEvent(req.cmdId(), it) }
    }

    private fun planExec(req: SubmittedInvokeAdhocReq): PlannedExec {
        return execCmdRepository.plan(
            ExecCmdRepository.PlanCmd(
                id = req.cmdId(),
                execId = req.execId,
                correlation = null,
                inputs = req.inputs.toExecInputs(),
                code = req.code,
            )
        )
    }

    private fun emitEvent(cmdId: CmdId, exec: PlannedExec) {
        eventEmitter.emit(cmdId, ExecPlannedEvent(exec))
    }
}