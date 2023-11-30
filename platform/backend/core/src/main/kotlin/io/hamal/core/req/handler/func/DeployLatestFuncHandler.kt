package io.hamal.core.req.handler.func

import io.hamal.core.event.PlatformEventEmitter
import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.repository.api.CodeQueryRepository
import io.hamal.repository.api.Func
import io.hamal.repository.api.FuncRepository
import io.hamal.repository.api.event.FuncDeployedEvent
import io.hamal.repository.api.submitted_req.FuncDeployLatestSubmitted
import org.springframework.stereotype.Component

@Component
class DeployLatestFuncHandler(
    val funcRepository: FuncRepository,
    val codeQueryRepository: CodeQueryRepository,
    val eventEmitter: PlatformEventEmitter
) : ReqHandler<FuncDeployLatestSubmitted>(FuncDeployLatestSubmitted::class) {

    override fun invoke(req: FuncDeployLatestSubmitted) {
        deployLatestVersion(req).also { emitEvent(req.cmdId(), it) }
    }
}

private fun DeployLatestFuncHandler.deployLatestVersion(req: FuncDeployLatestSubmitted): Func {
    return funcRepository.deployLatest(req.funcId, req.cmdId(), req.deployMessage)
}

private fun DeployLatestFuncHandler.emitEvent(cmdId: CmdId, func: Func) {
    eventEmitter.emit(cmdId, FuncDeployedEvent(func))
}