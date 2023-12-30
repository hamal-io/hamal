package io.hamal.core.adapter

import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.ReqStatus.Submitted
import io.hamal.lib.domain.vo.FeedbackId
import io.hamal.lib.domain.vo.ReqId
import io.hamal.repository.api.Feedback
import io.hamal.repository.api.FeedbackQueryRepository
import io.hamal.repository.api.FeedbackQueryRepository.FeedbackQuery
import io.hamal.repository.api.ReqCmdRepository
import io.hamal.repository.api.submitted_req.FeedbackCreateSubmitted
import io.hamal.request.FeedbackCreateReq
import org.springframework.stereotype.Component

interface FeedbackCreatePort {
    operator fun <T : Any> invoke(
        req: FeedbackCreateReq,
        responseHandler: (FeedbackCreateSubmitted) -> T
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
    private val generateDomainId: GenerateDomainId,
    private val reqCmdRepository: ReqCmdRepository

) : FeedbackPort {
    override fun <T : Any> invoke(
        req: FeedbackCreateReq,
        responseHandler: (FeedbackCreateSubmitted) -> T
    )
            : T {
        return FeedbackCreateSubmitted(
            id = generateDomainId(::ReqId),
            status = Submitted,
            mood = req.mood,
            message = req.message,
            accountId = req.accountId
        ).also(reqCmdRepository::queue).let(responseHandler)
    }

    override fun <T : Any> invoke(feedbackId: FeedbackId, responseHandler: (Feedback) -> T): T {
        return responseHandler(feedbackQueryRepository.get(feedbackId))
    }

    override fun <T : Any> invoke(
        query: FeedbackQuery,
        responseHandler: (List<Feedback>) -> T
    ): T {
        TODO("142")
    }
}