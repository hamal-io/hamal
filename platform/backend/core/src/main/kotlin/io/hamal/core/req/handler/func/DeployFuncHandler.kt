package io.hamal.core.req.handler.func

import io.hamal.core.event.PlatformEventEmitter
import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.repository.api.Func
import io.hamal.repository.api.FuncCmdRepository
import io.hamal.repository.api.FuncRepository
import io.hamal.repository.api.event.FuncUpdatedEvent
import io.hamal.repository.api.submitted_req.FuncDeploySubmitted
import org.springframework.stereotype.Component

@Component
class DeployFuncHandler(
    val funcRepository: FuncRepository,
    val eventEmitter: PlatformEventEmitter
) : ReqHandler<FuncDeploySubmitted>(FuncDeploySubmitted::class) {

    override fun invoke(req: FuncDeploySubmitted) {
        deployVersion(req).also { emitEvent(req.cmdId(), it) }
    }
}

//FIXME-53 - validation
private fun DeployFuncHandler.deployVersion(req: FuncDeploySubmitted): Func {
    val func = funcRepository.get(req.funcId)
    return funcRepository.deploy(
        func.id,
        FuncCmdRepository.DeployCmd(
            id = req.cmdId(),
            versionToDeploy = req.versionToDeploy
        )
    )
}

private fun DeployFuncHandler.emitEvent(cmdId: CmdId, func: Func) {
    eventEmitter.emit(cmdId, FuncUpdatedEvent(func))
}