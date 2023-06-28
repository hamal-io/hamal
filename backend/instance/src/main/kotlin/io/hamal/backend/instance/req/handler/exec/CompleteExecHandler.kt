package io.hamal.backend.instance.req.handler.exec

import io.hamal.backend.instance.event.ExecutionCompletedEvent
import io.hamal.backend.instance.event.SystemEventEmitter
import io.hamal.backend.instance.req.ReqHandler
import io.hamal.backend.instance.req.handler.cmdId
import io.hamal.backend.repository.api.ExecCmdRepository
import io.hamal.backend.repository.api.ExecQueryRepository
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.CompletedExec
import io.hamal.lib.domain.StartedExec
import io.hamal.lib.domain.req.SubmittedCompleteExecReq
import org.springframework.stereotype.Component

@Component
class CompleteExecHandler(
    private val execQueryRepository: ExecQueryRepository,
    private val execCmdRepository: ExecCmdRepository,
    private val eventEmitter: SystemEventEmitter<*>
) : ReqHandler<SubmittedCompleteExecReq>(SubmittedCompleteExecReq::class) {

    override fun invoke(req: SubmittedCompleteExecReq) {
        val cmdId = req.cmdId()

        val exec = execQueryRepository.get(req.execId)
        require(exec is StartedExec) { "Exec not in status Started" }

//        if (exec.correlation != null) {
//            stateCmdService.set(
//                cmdId, StateCmdService.StateToSet(
//                    correlation = exec.correlation!!,
//                    payload = req.state
//                )
//            )
//        }

        completeExec(req)
            // FIXME also set state
            // FIXME also emit events in req
            .also { emitEvent(cmdId, it) }
    }

    private fun completeExec(req: SubmittedCompleteExecReq) =
        execCmdRepository.complete(ExecCmdRepository.CompleteCmd(req.cmdId(), req.execId))

    private fun emitEvent(cmdId: CmdId, exec: CompletedExec) {
        eventEmitter.emit(cmdId, ExecutionCompletedEvent(completedExec = exec))
    }
}