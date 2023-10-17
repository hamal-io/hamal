package io.hamal.core.req.handler.snippet

import io.hamal.core.event.PlatformEventEmitter
import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.repository.api.Snippet
import io.hamal.repository.api.SnippetCmdRepository
import io.hamal.repository.api.event.SnippetCreatedEvent
import io.hamal.repository.api.submitted_req.SubmittedCreateSnippetReq
import org.springframework.stereotype.Component

@Component
class CreateSnippetHandler(
    val snippetCmdRepository: SnippetCmdRepository,
    val eventEmitter: PlatformEventEmitter,
) : ReqHandler<SubmittedCreateSnippetReq>(SubmittedCreateSnippetReq::class) {
    override fun invoke(req: SubmittedCreateSnippetReq) {
        createSnippet(req).also { emitEvent(req.cmdId(), it) }
    }
}

    private fun CreateSnippetHandler.createSnippet(req: SubmittedCreateSnippetReq): Snippet {
        return snippetCmdRepository.create(
            SnippetCmdRepository.CreateCmd(
                id = req.cmdId(),
                snippetId = req.id,
                groupId = req.groupId,
                name = req.name,
                inputs = req.inputs,
                value = req.value,
                accountId = req.accountId
            )
        )
    }

    private fun CreateSnippetHandler.emitEvent(cmdId: CmdId, snippet: Snippet) {
        eventEmitter.emit(cmdId, SnippetCreatedEvent(snippet))
    }