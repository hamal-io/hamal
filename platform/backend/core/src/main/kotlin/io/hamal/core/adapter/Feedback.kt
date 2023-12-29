package io.hamal.core.adapter

import io.hamal.lib.domain.vo.FeedbackId
import io.hamal.repository.api.Feedback
import io.hamal.repository.api.FeedbackCmdRepository
import io.hamal.repository.api.FeedbackQueryRepository.FeedbackQuery
import io.hamal.request.FeedbackCreateReq
import org.springframework.stereotype.Component

interface FeedbackCreatePort {
    operator fun <T : Any> invoke(
        req: FeedbackCreateReq,
        responseHandler: (FeedbackCreateReq) -> T
    ): T
}

interface FeedbackGetPort {
    operator fun <T : Any> invoke(
        feedbackId: FeedbackId,
        responseHandler: (FeedbackId) -> T
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
    private val feedbackCmdRepository: FeedbackCmdRepository
) : FeedbackPort {
    override fun <T : Any> invoke(req: FeedbackCreateReq, responseHandler: (FeedbackCreateReq) -> T): T {
        TODO("Not yet implemented")
    }

    override fun <T : Any> invoke(feedbackId: FeedbackId, responseHandler: (FeedbackId) -> T): T {
        TODO("Not yet implemented")
    }

    override fun <T : Any> invoke(
        query: FeedbackQuery,
        responseHandler: (List<Feedback>) -> T
    ): T {
        TODO("Not yet implemented")
    }
}