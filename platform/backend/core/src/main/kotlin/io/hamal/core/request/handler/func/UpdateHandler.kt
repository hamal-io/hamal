package io.hamal.core.request.handler.func

import io.hamal.core.event.InternalEventEmitter
import io.hamal.core.request.RequestHandler
import io.hamal.core.request.handler.cmdId
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
    private val codeRepository: CodeRepository,
    private val funcRepository: FuncRepository,
    private val eventEmitter: InternalEventEmitter
) : RequestHandler<FuncUpdateRequested>(FuncUpdateRequested::class) {

    override fun invoke(req: FuncUpdateRequested) {
        updateFunc(req)
            .also { emitEvent(req.cmdId(), it) }
    }

    private fun updateFunc(req: FuncUpdateRequested): Func {
        val func = funcRepository.get(req.id)

        val code = codeRepository.update(
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
                codeVersion = code.version
            )
        )
    }

    private fun emitEvent(cmdId: CmdId, func: Func) {
        eventEmitter.emit(cmdId, FuncUpdatedEvent(func))
    }

}

