package io.hamal.core.req.handler.snippet

import io.hamal.core.event.PlatformEventEmitter
import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.repository.api.Snippet
import io.hamal.repository.api.SnippetCmdRepository.*
import io.hamal.repository.api.SnippetRepository
import io.hamal.repository.api.event.SnippetCreatedEvent
import io.hamal.repository.api.submitted_req.SnippetUpdateSubmittedReq
import org.springframework.stereotype.Component

@Component
class UpdateSnippetHandler(
    val snippetRepository: SnippetRepository,
    val eventEmitter: PlatformEventEmitter
) : ReqHandler<SnippetUpdateSubmittedReq>(SnippetUpdateSubmittedReq::class) {

    override fun invoke(req: SnippetUpdateSubmittedReq) {
        updateSnippet(req).also { emitEvent(req.cmdId(), it) }
    }
}

private fun UpdateSnippetHandler.updateSnippet(req: SnippetUpdateSubmittedReq): Snippet {
    return snippetRepository.update(
        req.id, UpdateCmd(
            id = req.cmdId(),
            name = req.name,
            inputs = req.inputs,
            value = req.value
        )
    )
}

private fun UpdateSnippetHandler.emitEvent(cmdId: CmdId, snippet: Snippet) {
    eventEmitter.emit(cmdId, SnippetCreatedEvent(snippet))
}