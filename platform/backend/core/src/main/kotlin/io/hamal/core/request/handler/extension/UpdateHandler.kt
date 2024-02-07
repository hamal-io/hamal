package io.hamal.core.request.handler.extension

import io.hamal.core.event.InternalEventEmitter
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.request.ExtensionUpdateRequested
import io.hamal.repository.api.CodeCmdRepository
import io.hamal.repository.api.Extension
import io.hamal.repository.api.ExtensionCmdRepository.UpdateCmd
import io.hamal.repository.api.ExtensionCode
import io.hamal.repository.api.ExtensionRepository
import io.hamal.repository.api.event.ExtensionUpdatedEvent
import org.springframework.stereotype.Component

@Component
class ExtensionUpdateHandler(
    val extensionRepository: ExtensionRepository,
    val codeCmdRepository: CodeCmdRepository,
    val eventEmitter: InternalEventEmitter
) : io.hamal.core.request.RequestHandler<ExtensionUpdateRequested>(ExtensionUpdateRequested::class) {

    override fun invoke(req: ExtensionUpdateRequested) {
        updateExtension(req).also { emitEvent(req.cmdId(), it) }
    }
}

private fun ExtensionUpdateHandler.updateExtension(req: ExtensionUpdateRequested): Extension {
    val ext = extensionRepository.get(req.extensionId)
    val code = codeCmdRepository.update(
        ext.code.id, CodeCmdRepository.UpdateCmd(
            id = req.cmdId(),
            value = req.code
        )
    )

    return extensionRepository.update(
        req.extensionId, UpdateCmd(
            id = req.cmdId(),
            name = req.name,
            code = ExtensionCode(
                id = code.id,
                version = code.version
            )
        )
    )
}

private fun ExtensionUpdateHandler.emitEvent(cmdId: CmdId, ext: Extension) {
    eventEmitter.emit(cmdId, ExtensionUpdatedEvent(ext))
}