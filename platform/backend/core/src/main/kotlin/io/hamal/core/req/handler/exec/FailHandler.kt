package io.hamal.core.req.handler.exec

import io.hamal.core.event.PlatformEventEmitter
import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.submitted.ExecFailSubmitted
import io.hamal.repository.api.ExecCmdRepository.FailCmd
import io.hamal.repository.api.FailedExec
import io.hamal.repository.api.StartedExec
import io.hamal.repository.api.event.ExecutionFailedEvent
import org.springframework.stereotype.Component

@Component
class ExecFailHandler(
    private val execQueryRepository: io.hamal.repository.api.ExecQueryRepository,
    private val execCmdRepository: io.hamal.repository.api.ExecCmdRepository,
    private val eventEmitter: PlatformEventEmitter
) : ReqHandler<ExecFailSubmitted>(ExecFailSubmitted::class) {

    override fun invoke(req: ExecFailSubmitted) {
        val cmdId = req.cmdId()

        val exec = execQueryRepository.get(req.execId)
        require(exec is StartedExec) { "Exec not in status Started" }

        failExec(req).also { emitFailedEvent(cmdId, it) }
    }

    private fun failExec(req: ExecFailSubmitted) =
        execCmdRepository.fail(FailCmd(req.cmdId(), req.execId, req.result))

    private fun emitFailedEvent(cmdId: CmdId, exec: FailedExec) {
        eventEmitter.emit(cmdId, ExecutionFailedEvent(exec))
    }
}
