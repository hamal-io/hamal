package io.hamal.core.adapter.feedback

import io.hamal.lib.domain.vo.FeedbackId
import io.hamal.repository.api.Feedback
import io.hamal.repository.api.FeedbackQueryRepository
import org.springframework.stereotype.Component

fun interface FeedbackGetPort {
    operator fun invoke(feedbackId: FeedbackId): Feedback
}

@Component
class FeedbackGetAdapter(
    private val feedbackQueryRepository: FeedbackQueryRepository,
) : FeedbackGetPort {
    override fun invoke(feedbackId: FeedbackId): Feedback = feedbackQueryRepository.get(feedbackId)
}