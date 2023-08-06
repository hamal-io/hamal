package io.hamal.backend.instance.req.handler.func

import io.hamal.backend.instance.event.FuncCreatedEvent
import io.hamal.backend.instance.event.SystemEventEmitter
import io.hamal.backend.instance.req.ReqHandler
import io.hamal.backend.instance.req.handler.cmdId
import io.hamal.backend.repository.api.FuncCmdRepository
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.Func
import io.hamal.lib.domain.req.SubmittedUpdateFuncReq
import org.springframework.stereotype.Component

@Component
class UpdateFuncHandler(
    val funcCmdRepository: FuncCmdRepository,
    val eventEmitter: SystemEventEmitter<*>
) : ReqHandler<SubmittedUpdateFuncReq>(SubmittedUpdateFuncReq::class) {

    override fun invoke(req: SubmittedUpdateFuncReq) {
        updateFunc(req).also { emitEvent(req.cmdId(), it) }
    }
}

private fun UpdateFuncHandler.updateFunc(req: SubmittedUpdateFuncReq): Func {
    return funcCmdRepository.update(
        FuncCmdRepository.UpdateCmd(
            id = req.cmdId(),
            funcId = req.id,
            name = req.name,
            inputs = req.inputs,
            code = req.code
        )
    )
}

private fun UpdateFuncHandler.emitEvent(cmdId: CmdId, func: Func) {
    eventEmitter.emit(cmdId, FuncCreatedEvent(func))
}
