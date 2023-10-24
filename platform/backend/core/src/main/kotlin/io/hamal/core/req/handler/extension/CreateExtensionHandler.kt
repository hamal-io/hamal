package io.hamal.core.req.handler.extension

import io.hamal.core.event.PlatformEventEmitter
import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.repository.api.Extension
import io.hamal.repository.api.ExtensionCmdRepository
import io.hamal.repository.api.ExtensionCmdRepository.*
import io.hamal.repository.api.ExtensionCode
import io.hamal.repository.api.event.ExtensionCreatedEvent
import io.hamal.repository.api.submitted_req.SubmittedCreateExtensionReq
import io.hamal.repository.api.submitted_req.SubmittedCreateSnippetReq
import org.springframework.stereotype.Component

@Component
class CreateExtensionHandler(
    val extensionCmdRepository: ExtensionCmdRepository,
    val eventEmitter: PlatformEventEmitter,
) : ReqHandler<SubmittedCreateExtensionReq>(SubmittedCreateExtensionReq::class) {
    override fun invoke(req: SubmittedCreateExtensionReq) {
        createExtension(req).also { emitEvent(req.cmdId(), it) }
    }
}

private fun CreateExtensionHandler.createExtension(req: SubmittedCreateExtensionReq): Extension {
    return extensionCmdRepository.create(
        CreateCmd(
            id = req.cmdId(),
            extId = req.id,
            groupId = req.groupId,
            name = req.name,
            code = ExtensionCode(
                req.codeId,
                req.codeVersion
            )
        )
    )
}

private fun CreateExtensionHandler.emitEvent(cmdId: CmdId, ext: Extension) {
    eventEmitter.emit(cmdId, ExtensionCreatedEvent(ext))
}