package io.hamal.core.req.handler.func

import io.hamal.core.event.PlatformEventEmitter
import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.request.FuncUpdateRequested
import io.hamal.repository.api.CodeCmdRepository
import io.hamal.repository.api.CodeRepository
import io.hamal.repository.api.Func
import io.hamal.repository.api.FuncCmdRepository.UpdateCmd
import io.hamal.repository.api.FuncRepository
import io.hamal.repository.api.event.FuncUpdatedEvent
import org.springframework.stereotype.Component


@Component
class FuncUpdateHandler(
    val codeRepository: CodeRepository,
    val funcRepository: FuncRepository,
    val eventEmitter: PlatformEventEmitter
) : ReqHandler<FuncUpdateRequested>(FuncUpdateRequested::class) {

    override fun invoke(req: FuncUpdateRequested) {
        updateFunc(req).also { emitEvent(req.cmdId(), it) }
    }
}

private fun FuncUpdateHandler.updateFunc(req: FuncUpdateRequested): Func {
    val func = funcRepository.get(req.funcId)

    val code = codeRepository.update(
        func.code.id, CodeCmdRepository.UpdateCmd(
            id = req.cmdId(),
            value = req.code
        )
    )

    return funcRepository.update(
        req.funcId,
        UpdateCmd(
            id = req.cmdId(),
            name = req.name,
            inputs = req.inputs,
            codeVersion = code.version
        )
    )
}

private fun FuncUpdateHandler.emitEvent(cmdId: CmdId, func: Func) {
    eventEmitter.emit(cmdId, FuncUpdatedEvent(func))
}
