package io.hamal.core.request.handler.func

import io.hamal.core.event.InternalEventEmitter
import io.hamal.core.request.RequestHandler
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.request.FuncCreateRequested
import io.hamal.repository.api.CodeCmdRepository
import io.hamal.repository.api.Func
import io.hamal.repository.api.FuncCmdRepository
import io.hamal.repository.api.FuncCmdRepository.CreateCmd
import io.hamal.repository.api.NamespaceQueryRepository
import io.hamal.repository.api.event.FuncCreatedEvent
import org.springframework.stereotype.Component

@Component
class FuncCreateHandler(
    private val codeCmdRepository: CodeCmdRepository,
    private val funcCmdRepository: FuncCmdRepository,
    private val eventEmitter: InternalEventEmitter,
    private val namespaceQueryRepository: NamespaceQueryRepository
) : RequestHandler<FuncCreateRequested>(FuncCreateRequested::class) {

    override fun invoke(req: FuncCreateRequested) {
        createFunc(req)
            .also { emitEvent(req.cmdId(), it) }
    }

    private fun createFunc(req: FuncCreateRequested): Func {
        val code = codeCmdRepository.create(
            CodeCmdRepository.CreateCmd(
                id = req.cmdId(),
                codeId = req.codeId,
                workspaceId = req.workspaceId,
                value = req.code
            )
        )
        return funcCmdRepository.create(
            CreateCmd(
                id = req.cmdId(),
                funcId = req.funcId,
                workspaceId = req.workspaceId,
                namespaceId = req.namespaceId,
                name = req.name,
                inputs = req.inputs,
                codeId = code.id,
                codeVersion = code.version
            )
        )
    }

    private fun emitEvent(cmdId: CmdId, func: Func) {
        eventEmitter.emit(cmdId, FuncCreatedEvent(func))
    }

}