package io.hamal.core.req.handler.extension

import io.hamal.core.event.PlatformEventEmitter
import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.repository.api.Extension
import io.hamal.repository.api.ExtensionCmdRepository.UpdateCmd
import io.hamal.repository.api.ExtensionCode
import io.hamal.repository.api.ExtensionRepository
import io.hamal.repository.api.event.ExtensionUpdatedEvent
import io.hamal.repository.api.submitted_req.SubmittedUpdateExtensionReq
import org.springframework.stereotype.Component

@Component
class UpdateExtensionHandler(
    val extensionRepository: ExtensionRepository,
    val eventEmitter: PlatformEventEmitter
) : ReqHandler<SubmittedUpdateExtensionReq>(SubmittedUpdateExtensionReq::class) {

    override fun invoke(req: SubmittedUpdateExtensionReq) {
        updateExtension(req).also { emitEvent(req.cmdId(), it) }
    }
}

private fun UpdateExtensionHandler.updateExtension(req: SubmittedUpdateExtensionReq): Extension {
    return extensionRepository.update(
        req.id, UpdateCmd(
            id = req.cmdId(),
            name = req.name,
            code = ExtensionCode(
                id = req.codeId ?: extensionRepository.get(req.id).code.id,
                version = req.codeVersion ?: extensionRepository.get(req.id).code.version
            )
        )
    )
}

private fun UpdateExtensionHandler.emitEvent(cmdId: CmdId, ext: Extension) {
    eventEmitter.emit(cmdId, ExtensionUpdatedEvent(ext))
}