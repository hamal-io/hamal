package io.hamal.core.req.handler.extension

import io.hamal.core.event.PlatformEventEmitter
import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.repository.api.CodeCmdRepository
import io.hamal.repository.api.Extension
import io.hamal.repository.api.ExtensionCmdRepository
import io.hamal.repository.api.ExtensionCmdRepository.CreateCmd
import io.hamal.repository.api.ExtensionCode
import io.hamal.repository.api.event.ExtensionCreatedEvent
import io.hamal.repository.api.submitted_req.ExtensionCreateSubmitted
import org.springframework.stereotype.Component

@Component
class CreateExtensionHandler(
    val extensionCmdRepository: ExtensionCmdRepository,
    val codeCmdRepository: CodeCmdRepository,
    val eventEmitter: PlatformEventEmitter,
) : ReqHandler<ExtensionCreateSubmitted>(ExtensionCreateSubmitted::class) {
    override fun invoke(req: ExtensionCreateSubmitted) {
        createExtension(req).also { emitEvent(req.cmdId(), it) }
    }
}

private fun CreateExtensionHandler.createExtension(req: ExtensionCreateSubmitted): Extension {
    val code = codeCmdRepository.create(
        CodeCmdRepository.CreateCmd(
            id = req.cmdId(),
            codeId = req.codeId,
            groupId = req.groupId,
            value = req.code
        )
    )
    return extensionCmdRepository.create(
        CreateCmd(
            id = req.cmdId(),
            extId = req.extensionId,
            groupId = req.groupId,
            name = req.name,
            code = ExtensionCode(
                code.id,
                code.version
            )
        )
    )
}

private fun CreateExtensionHandler.emitEvent(cmdId: CmdId, ext: Extension) {
    eventEmitter.emit(cmdId, ExtensionCreatedEvent(ext))
}