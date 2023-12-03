package io.hamal.core.req.handler.extension

import io.hamal.core.event.PlatformEventEmitter
import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.repository.api.CodeCmdRepository
import io.hamal.repository.api.Extension
import io.hamal.repository.api.ExtensionCmdRepository.UpdateCmd
import io.hamal.repository.api.ExtensionCode
import io.hamal.repository.api.ExtensionRepository
import io.hamal.repository.api.event.ExtensionUpdatedEvent
import io.hamal.repository.api.submitted_req.ExtensionUpdateSubmitted
import org.springframework.stereotype.Component

@Component
class ExtensionUpdateHandler(
    val extensionRepository: ExtensionRepository,
    val codeCmdRepository: CodeCmdRepository,
    val eventEmitter: PlatformEventEmitter
) : ReqHandler<ExtensionUpdateSubmitted>(ExtensionUpdateSubmitted::class) {

    override fun invoke(req: ExtensionUpdateSubmitted) {
        updateExtension(req).also { emitEvent(req.cmdId(), it) }
    }
}

private fun ExtensionUpdateHandler.updateExtension(req: ExtensionUpdateSubmitted): Extension {
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