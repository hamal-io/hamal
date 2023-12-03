package io.hamal.core.req.handler.func

import io.hamal.core.event.PlatformEventEmitter
import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.DeployMessage
import io.hamal.repository.api.CodeQueryRepository
import io.hamal.repository.api.Func
import io.hamal.repository.api.FuncCmdRepository.DeployCmd
import io.hamal.repository.api.FuncRepository
import io.hamal.repository.api.event.FuncDeployedEvent
import io.hamal.repository.api.submitted_req.FuncDeploySubmitted
import org.springframework.stereotype.Component

@Component
class DeployFuncHandler(
    val funcRepository: FuncRepository,
    val codeQueryRepository: CodeQueryRepository,
    val eventEmitter: PlatformEventEmitter
) : ReqHandler<FuncDeploySubmitted>(FuncDeploySubmitted::class) {

    override fun invoke(req: FuncDeploySubmitted) {
        deployVersion(req).also { emitEvent(req.cmdId(), it) }
    }
}

private fun DeployFuncHandler.deployVersion(req: FuncDeploySubmitted): Func {
    val func = funcRepository.get(req.funcId)

    return funcRepository.deploy(
        req.funcId,
        DeployCmd(
            id = req.cmdId(),
            version = req.versionToDeploy ?: func.code.version,
            message = req.deployMessage ?: DeployMessage.empty
        )
    )
}

private fun DeployFuncHandler.emitEvent(cmdId: CmdId, func: Func) {
    eventEmitter.emit(cmdId, FuncDeployedEvent(func))
}
