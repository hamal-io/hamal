package io.hamal.core.adapter

import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus.Submitted
import io.hamal.lib.domain.request.FeedbackCreateRequest
import io.hamal.lib.domain.request.FeedbackCreateRequested
import io.hamal.lib.domain.vo.FeedbackId
import io.hamal.lib.domain.vo.RequestId
import io.hamal.repository.api.Feedback
import io.hamal.repository.api.FeedbackQueryRepository
import io.hamal.repository.api.FeedbackQueryRepository.FeedbackQuery
import io.hamal.repository.api.RequestCmdRepository
import org.springframework.stereotype.Component

interface FeedbackCreatePort {
    operator fun invoke(req: FeedbackCreateRequest): FeedbackCreateRequested
}

interface FeedbackGetPort {
    operator fun invoke(feedbackId: FeedbackId): Feedback
}

interface FeedbackListPort {
    operator fun invoke(query: FeedbackQuery): List<Feedback>
}

interface FeedbackPort : FeedbackCreatePort, FeedbackGetPort, FeedbackListPort

@Component
class FeedbackAdapter(
    private val feedbackQueryRepository: FeedbackQueryRepository,
    private val generateDomainId: GenerateDomainId,
    private val requestCmdRepository: RequestCmdRepository

) : FeedbackPort {

    override fun invoke(req: FeedbackCreateRequest): FeedbackCreateRequested {
        return FeedbackCreateRequested(
            id = generateDomainId(::RequestId),
            status = Submitted,
            feedbackId = generateDomainId(::FeedbackId),
            mood = req.mood,
            message = req.message,
            accountId = req.accountId
        ).also(requestCmdRepository::queue)
    }

    override fun invoke(feedbackId: FeedbackId): Feedback = feedbackQueryRepository.get(feedbackId)

    override fun invoke(query: FeedbackQuery): List<Feedback> = feedbackQueryRepository.list(query)
}