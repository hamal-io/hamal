package io.hamal.core.request.handler.feedback

import io.hamal.core.event.PlatformEventEmitter
import io.hamal.core.request.RequestHandler
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.request.FeedbackCreateRequested
import io.hamal.repository.api.Feedback
import io.hamal.repository.api.FeedbackCmdRepository
import io.hamal.repository.api.FeedbackCmdRepository.CreateCmd
import io.hamal.repository.api.event.FeedbackCreatedEvent
import org.springframework.stereotype.Component

@Component
class FeedbackCreateHandler(
    val feedbackCmdRepository: FeedbackCmdRepository,
    val eventEmitter: PlatformEventEmitter
) : RequestHandler<FeedbackCreateRequested>(FeedbackCreateRequested::class) {
    override fun invoke(req: FeedbackCreateRequested) {
        createFeedback(req).also { emitEvent(req.cmdId(), it) }
    }
}

private fun FeedbackCreateHandler.createFeedback(req: FeedbackCreateRequested): Feedback {
    return feedbackCmdRepository.create(
        CreateCmd(
            id = req.cmdId(),
            feedbackId = req.feedbackId,
            mood = req.mood,
            message = req.message,
            accountId = req.accountId
        )
    )
}

private fun FeedbackCreateHandler.emitEvent(cmdId: CmdId, feedback: Feedback) {
    eventEmitter.emit(cmdId, FeedbackCreatedEvent(feedback))
}