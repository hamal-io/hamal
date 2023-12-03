package io.hamal.core.req.handler.func

import io.hamal.core.event.PlatformEventEmitter
import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.repository.api.CodeQueryRepository
import io.hamal.repository.api.Func
import io.hamal.repository.api.FuncCmdRepository
import io.hamal.repository.api.FuncRepository
import io.hamal.repository.api.event.FuncDeployedEvent
import io.hamal.repository.api.submitted_req.FuncDeploySubmitted
import org.springframework.stereotype.Component

@Component
class FuncDeployHandler(
    val funcRepository: FuncRepository,
    val codeQueryRepository: CodeQueryRepository,
    val eventEmitter: PlatformEventEmitter
) : ReqHandler<FuncDeploySubmitted>(FuncDeploySubmitted::class) {

    override fun invoke(req: FuncDeploySubmitted) {
        deployVersion(req).also { emitEvent(req.cmdId(), it) }
    }
}

private fun FuncDeployHandler.deployVersion(req: FuncDeploySubmitted): Func {
    return req.versionToDeploy
        .let { funcRepository.deploy(req.funcId, FuncCmdRepository.DeployCmd(id = req.cmdId(), versionToDeploy = it)) }
}

private fun FuncDeployHandler.emitEvent(cmdId: CmdId, func: Func) {
    eventEmitter.emit(cmdId, FuncDeployedEvent(func))
}