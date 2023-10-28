package io.hamal.core.req.handler.func

import io.hamal.core.event.PlatformEventEmitter
import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.repository.api.CodeCmdRepository
import io.hamal.repository.api.Func
import io.hamal.repository.api.FuncCmdRepository.UpdateCmd
import io.hamal.repository.api.FuncCode
import io.hamal.repository.api.FuncRepository
import io.hamal.repository.api.event.FuncCreatedEvent
import io.hamal.repository.api.submitted_req.FuncUpdateSubmittedReq
import org.springframework.stereotype.Component

@Component
class UpdateFuncHandler(
    val codeCmdRepository: CodeCmdRepository,
    val funcRepository: FuncRepository,
    val eventEmitter: PlatformEventEmitter
) : ReqHandler<FuncUpdateSubmittedReq>(FuncUpdateSubmittedReq::class) {

    override fun invoke(req: FuncUpdateSubmittedReq) {
        updateFunc(req).also { emitEvent(req.cmdId(), it) }
    }
}

private fun UpdateFuncHandler.updateFunc(req: FuncUpdateSubmittedReq): Func {
    val func = funcRepository.get(req.id)
    val code = codeCmdRepository.update(
        func.code.id, CodeCmdRepository.UpdateCmd(
            id = req.cmdId(),
            value = req.code
        )
    )

    return funcRepository.update(
        req.id,
        UpdateCmd(
            id = req.cmdId(),
            name = req.name,
            inputs = req.inputs,
            code = FuncCode(
                id = code.id,
                version = code.version
            )
        )
    )
}

private fun UpdateFuncHandler.emitEvent(cmdId: CmdId, func: Func) {
    eventEmitter.emit(cmdId, FuncCreatedEvent(func))
}
