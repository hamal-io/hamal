package io.hamal.core.request.handler.func

import io.hamal.core.event.InternalEventEmitter
import io.hamal.core.request.RequestHandler
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.request.FuncDeployRequested
import io.hamal.lib.domain.vo.DeployMessage
import io.hamal.repository.api.CodeQueryRepository
import io.hamal.repository.api.Func
import io.hamal.repository.api.FuncCmdRepository.DeployCmd
import io.hamal.repository.api.FuncRepository
import io.hamal.repository.api.event.FuncDeployedEvent
import org.springframework.stereotype.Component

@Component
class FuncDeployHandler(
    private val funcRepository: FuncRepository,
    private val codeQueryRepository: CodeQueryRepository,
    private val eventEmitter: InternalEventEmitter
) : RequestHandler<FuncDeployRequested>(FuncDeployRequested::class) {

    override fun invoke(req: FuncDeployRequested) {
        deployVersion(req)
            .also { emitEvent(req.cmdId(), it) }
    }

    private fun deployVersion(req: FuncDeployRequested): Func {
        val func = funcRepository.get(req.id)

        return funcRepository.deploy(
            req.id,
            DeployCmd(
                id = req.cmdId(),
                version = req.version ?: func.code.version,
                message = req.message ?: DeployMessage.empty
            )
        )
    }

    private fun emitEvent(cmdId: CmdId, func: Func) {
        eventEmitter.emit(cmdId, FuncDeployedEvent(func))
    }
}
