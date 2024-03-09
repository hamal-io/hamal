package io.hamal.core.request.handler.exec

import io.hamal.core.event.InternalEventEmitter
import io.hamal.core.request.RequestHandler
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.request.ExecFailRequested
import io.hamal.repository.api.Exec
import io.hamal.repository.api.ExecCmdRepository.FailCmd
import io.hamal.repository.api.event.ExecFailedEvent
import org.springframework.stereotype.Component

@Component
class ExecFailHandler(
    private val execQueryRepository: io.hamal.repository.api.ExecQueryRepository,
    private val execCmdRepository: io.hamal.repository.api.ExecCmdRepository,
    private val eventEmitter: InternalEventEmitter
) : RequestHandler<ExecFailRequested>(ExecFailRequested::class) {

    override fun invoke(req: ExecFailRequested) {
        val cmdId = req.cmdId()

        val exec = execQueryRepository.get(req.id)
        require(exec is Exec.Started) { "Exec not in status Started" }

        failExec(req).also { emitFailedEvent(cmdId, it) }
    }

    private fun failExec(req: ExecFailRequested) =
        execCmdRepository.fail(FailCmd(req.cmdId(), req.id, req.result))

    private fun emitFailedEvent(cmdId: CmdId, exec: Exec.Failed) {
        eventEmitter.emit(cmdId, ExecFailedEvent(exec))
    }
}
