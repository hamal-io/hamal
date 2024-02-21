package io.hamal.core.adapter.feedback

import io.hamal.repository.api.Feedback
import io.hamal.repository.api.FeedbackQueryRepository
import io.hamal.repository.api.FeedbackQueryRepository.FeedbackQuery
import org.springframework.stereotype.Component

fun interface FeedbackListPort {
    operator fun invoke(query: FeedbackQuery): List<Feedback>
}

@Component
class FeedbackListAdapter(
    private val feedbackQueryRepository: FeedbackQueryRepository
) : FeedbackListPort {
    override fun invoke(query: FeedbackQuery): List<Feedback> = feedbackQueryRepository.list(query)
}