package io.hamal.core.adapter

import io.hamal.lib.domain.GenerateId
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
    operator fun <T : Any> invoke(
        req: FeedbackCreateRequest,
        responseHandler: (FeedbackCreateRequested) -> T
    ): T
}

interface FeedbackGetPort {
    operator fun <T : Any> invoke(
        feedbackId: FeedbackId,
        responseHandler: (Feedback) -> T
    ): T
}

interface FeedbackListPort {
    operator fun <T : Any> invoke(
        query: FeedbackQuery,
        responseHandler: (List<Feedback>) -> T
    ): T
}

interface FeedbackPort : FeedbackCreatePort, FeedbackGetPort, FeedbackListPort

@Component
class FeedbackAdapter(
    private val feedbackQueryRepository: FeedbackQueryRepository,
    private val generateDomainId: GenerateId,
    private val reqCmdRepository: RequestCmdRepository

) : FeedbackPort {

    override fun <T : Any> invoke(
        req: FeedbackCreateRequest,
        responseHandler: (FeedbackCreateRequested) -> T
    ): T {
        return FeedbackCreateRequested(
            id = generateDomainId(::RequestId),
            status = Submitted,
            mood = req.mood,
            message = req.message,
            accountId = req.accountId
        ).also(reqCmdRepository::queue).let(responseHandler)
    }

    override fun <T : Any> invoke(feedbackId: FeedbackId, responseHandler: (Feedback) -> T): T {
        return responseHandler(feedbackQueryRepository.get(feedbackId))
    }

    override fun <T : Any> invoke(query: FeedbackQuery, responseHandler: (List<Feedback>) -> T): T {
        return responseHandler(feedbackQueryRepository.list(query))
    }

}