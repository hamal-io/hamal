package io.hamal.backend.instance.req.handler.exec

import io.hamal.backend.instance.event.ExecutionFailedEvent
import io.hamal.backend.instance.event.SystemEventEmitter
import io.hamal.backend.instance.req.ReqHandler
import io.hamal.backend.instance.req.handler.cmdId
import io.hamal.backend.repository.api.ExecCmdRepository
import io.hamal.backend.repository.api.ExecQueryRepository
import io.hamal.backend.repository.api.FailedExec
import io.hamal.backend.repository.api.StartedExec
import io.hamal.backend.repository.api.submitted_req.SubmittedFailExecReq
import io.hamal.lib.common.domain.CmdId
import org.springframework.stereotype.Component

@Component
class FailExecHandler(
    private val execQueryRepository: ExecQueryRepository,
    private val execCmdRepository: ExecCmdRepository,
    private val eventEmitter: SystemEventEmitter
) : ReqHandler<SubmittedFailExecReq>(SubmittedFailExecReq::class) {

    override fun invoke(req: SubmittedFailExecReq) {
        val cmdId = req.cmdId()

        val exec = execQueryRepository.get(req.id)
        require(exec is StartedExec) { "Exec not in status Started" }

        failExec(req).also { emitFailedEvent(cmdId, it) }
    }

    private fun failExec(req: SubmittedFailExecReq) =
        execCmdRepository.fail(ExecCmdRepository.FailCmd(req.cmdId(), req.id, req.cause))

    private fun emitFailedEvent(cmdId: CmdId, exec: FailedExec) {
        eventEmitter.emit(cmdId, ExecutionFailedEvent(exec))
    }
}
