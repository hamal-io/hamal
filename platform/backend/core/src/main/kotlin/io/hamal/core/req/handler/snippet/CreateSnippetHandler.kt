package io.hamal.core.req.handler.snippet

import io.hamal.core.event.PlatformEventEmitter
import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.repository.api.Snippet
import io.hamal.repository.api.SnippetCmdRepository
import io.hamal.repository.api.event.SnippetCreatedEvent
import io.hamal.repository.api.submitted_req.SnippetCreateSubmittedReq
import org.springframework.stereotype.Component

@Component
class CreateSnippetHandler(
    val snippetCmdRepository: SnippetCmdRepository,
    val eventEmitter: PlatformEventEmitter,
) : ReqHandler<SnippetCreateSubmittedReq>(SnippetCreateSubmittedReq::class) {
    override fun invoke(req: SnippetCreateSubmittedReq) {
        createSnippet(req).also { emitEvent(req.cmdId(), it) }
    }
}

private fun CreateSnippetHandler.createSnippet(req: SnippetCreateSubmittedReq): Snippet {
    return snippetCmdRepository.create(
        SnippetCmdRepository.CreateCmd(
            id = req.cmdId(),
            snippetId = req.id,
            groupId = req.groupId,
            name = req.name,
            inputs = req.inputs,
            value = req.value,
            creatorId = req.creatorId
        )
    )
}

private fun CreateSnippetHandler.emitEvent(cmdId: CmdId, snippet: Snippet) {
    eventEmitter.emit(cmdId, SnippetCreatedEvent(snippet))
}