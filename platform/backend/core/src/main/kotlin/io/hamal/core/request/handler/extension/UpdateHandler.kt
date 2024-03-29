package io.hamal.core.request.handler.extension

import io.hamal.core.event.InternalEventEmitter
import io.hamal.core.request.RequestHandler
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
    private val extensionRepository: ExtensionRepository,
    private val codeCmdRepository: CodeCmdRepository,
    private val eventEmitter: InternalEventEmitter
) : RequestHandler<ExtensionUpdateRequested>(ExtensionUpdateRequested::class) {

    override fun invoke(req: ExtensionUpdateRequested) {
        updateExtension(req)
            .also { emitEvent(req.cmdId(), it) }
    }

    private fun updateExtension(req: ExtensionUpdateRequested): Extension {
        val ext = extensionRepository.get(req.id)
        val code = codeCmdRepository.update(
            ext.code.id, CodeCmdRepository.UpdateCmd(
                id = req.cmdId(),
                value = req.code
            )
        )

        return extensionRepository.update(
            req.id, UpdateCmd(
                id = req.cmdId(),
                name = req.name,
                code = ExtensionCode(
                    id = code.id,
                    version = code.version
                )
            )
        )
    }

    private fun emitEvent(cmdId: CmdId, ext: Extension) {
        eventEmitter.emit(cmdId, ExtensionUpdatedEvent(ext))
    }
}
