package io.hamal.backend.instance.req.handler.exec

import io.hamal.backend.instance.event.ExecutionFailedEvent
import io.hamal.backend.instance.event.InstanceEventEmitter
import io.hamal.backend.instance.req.ReqHandler
import io.hamal.backend.instance.req.handler.cmdId
import io.hamal.repository.api.submitted_req.SubmittedFailExecReq
import io.hamal.lib.common.domain.CmdId
import org.springframework.stereotype.Component

@Component
class FailExecHandler(
    private val execQueryRepository: io.hamal.repository.api.ExecQueryRepository,
    private val execCmdRepository: io.hamal.repository.api.ExecCmdRepository,
    private val eventEmitter: InstanceEventEmitter
) : ReqHandler<SubmittedFailExecReq>(SubmittedFailExecReq::class) {

    override fun invoke(req: SubmittedFailExecReq) {
        val cmdId = req.cmdId()

        val exec = execQueryRepository.get(req.id)
        require(exec is io.hamal.repository.api.StartedExec) { "Exec not in status Started" }

        failExec(req).also { emitFailedEvent(cmdId, it) }
    }

    private fun failExec(req: SubmittedFailExecReq) =
        execCmdRepository.fail(io.hamal.repository.api.ExecCmdRepository.FailCmd(req.cmdId(), req.id, req.cause))

    private fun emitFailedEvent(cmdId: CmdId, exec: io.hamal.repository.api.FailedExec) {
        eventEmitter.emit(cmdId, ExecutionFailedEvent(exec))
    }
}
