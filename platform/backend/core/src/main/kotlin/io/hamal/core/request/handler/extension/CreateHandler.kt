package io.hamal.core.request.handler.extension

import io.hamal.core.event.InternalEventEmitter
import io.hamal.core.request.RequestHandler
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.request.ExtensionCreateRequested
import io.hamal.repository.api.CodeCmdRepository
import io.hamal.repository.api.Extension
import io.hamal.repository.api.ExtensionCmdRepository
import io.hamal.repository.api.ExtensionCmdRepository.CreateCmd
import io.hamal.repository.api.ExtensionCode
import io.hamal.repository.api.event.ExtensionCreatedEvent
import org.springframework.stereotype.Component


@Component
class ExtensionCreateHandler(
    private val extensionCmdRepository: ExtensionCmdRepository,
    private val codeCmdRepository: CodeCmdRepository,
    private val eventEmitter: InternalEventEmitter,
) : RequestHandler<ExtensionCreateRequested>(ExtensionCreateRequested::class) {

    override fun invoke(req: ExtensionCreateRequested) {
        createExtension(req)
            .also { emitEvent(req.cmdId(), it) }
    }

    private fun createExtension(req: ExtensionCreateRequested): Extension {
        val code = codeCmdRepository.create(
            CodeCmdRepository.CreateCmd(
                id = req.cmdId(),
                codeId = req.codeId,
                workspaceId = req.workspaceId,
                value = req.code
            )
        )
        return extensionCmdRepository.create(
            CreateCmd(
                id = req.cmdId(),
                extensionId = req.id,
                workspaceId = req.workspaceId,
                name = req.name,
                code = ExtensionCode(
                    code.id,
                    code.version
                )
            )
        )
    }

    private fun emitEvent(cmdId: CmdId, ext: Extension) {
        eventEmitter.emit(cmdId, ExtensionCreatedEvent(ext))
    }
}

