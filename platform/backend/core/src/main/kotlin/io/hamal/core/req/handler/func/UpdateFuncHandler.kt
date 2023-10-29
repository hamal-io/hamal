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
import io.hamal.repository.api.event.FuncUpdatedEvent
import io.hamal.repository.api.submitted_req.FuncUpdateSubmitted
import org.springframework.stereotype.Component

@Component
class UpdateFuncHandler(
    val codeCmdRepository: CodeCmdRepository,
    val funcRepository: FuncRepository,
    val eventEmitter: PlatformEventEmitter
) : ReqHandler<FuncUpdateSubmitted>(FuncUpdateSubmitted::class) {

    override fun invoke(req: FuncUpdateSubmitted) {
        updateFunc(req).also { emitEvent(req.cmdId(), it) }
    }
}

private fun UpdateFuncHandler.updateFunc(req: FuncUpdateSubmitted): Func {
    val func = funcRepository.get(req.id)

    if (req.deployedVersion != null) {
        return funcRepository.update(
            req.id,
            UpdateCmd(
                id = req.cmdId(),
                name = func.name,
                inputs = func.inputs,
                code = FuncCode(
                    id = func.code.id,
                    version = func.code.version,
                    deployedVersion = req.deployedVersion!!
                )
            )
        )
    }

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
                version = code.version,
                deployedVersion = func.code.deployedVersion
            )
        )
    )
}

private fun UpdateFuncHandler.emitEvent(cmdId: CmdId, func: Func) {
    eventEmitter.emit(cmdId, FuncUpdatedEvent(func))
}
