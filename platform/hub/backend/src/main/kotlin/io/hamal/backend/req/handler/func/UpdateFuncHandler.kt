package io.hamal.backend.req.handler.func

import io.hamal.backend.event.HubEventEmitter
import io.hamal.backend.event.event.FuncCreatedEvent
import io.hamal.backend.req.ReqHandler
import io.hamal.backend.req.handler.cmdId
import io.hamal.repository.api.Func
import io.hamal.repository.api.FuncCmdRepository
import io.hamal.repository.api.submitted_req.SubmittedUpdateFuncReq
import io.hamal.lib.common.domain.CmdId
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
