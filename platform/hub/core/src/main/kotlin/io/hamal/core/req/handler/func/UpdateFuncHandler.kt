package io.hamal.core.req.handler.func

import io.hamal.core.event.HubEventEmitter
import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.repository.api.Func
import io.hamal.repository.api.FuncCmdRepository
import io.hamal.repository.api.event.FuncCreatedEvent
import io.hamal.repository.api.submitted_req.SubmittedUpdateFuncReq
import org.springframework.stereotype.Component

@Component
class UpdateFuncHandler(
    val funcCmdRepository: FuncCmdRepository,
    val eventEmitter: HubEventEmitter
) : ReqHandler<SubmittedUpdateFuncReq>(SubmittedUpdateFuncReq::class) {

    override fun invoke(req: SubmittedUpdateFuncReq) {
        updateFunc(req).also { emitEvent(req.cmdId(), it) }
    }
}

private fun UpdateFuncHandler.updateFunc(req: SubmittedUpdateFuncReq): Func {
    return funcCmdRepository.update(
        req.id,
        FuncCmdRepository.UpdateCmd(
            id = req.cmdId(),
            namespaceId = req.namespaceId,
            name = req.name,
            inputs = req.inputs,
            code = req.code
        )
    )
}

private fun UpdateFuncHandler.emitEvent(cmdId: CmdId, func: Func) {
    eventEmitter.emit(cmdId, FuncCreatedEvent(func))
}
