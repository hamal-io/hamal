package io.hamal.repository.memory.record.feedback

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.FeedbackId
import io.hamal.repository.api.Feedback
import io.hamal.repository.api.FeedbackQueryRepository.FeedbackQuery

internal object FeedbackCurrentProjection {
    private val projection = mutableMapOf<FeedbackId, Feedback>()

    fun apply(feedback: Feedback) {
        projection[feedback.id] = feedback
    }

    fun find(feedbackId: FeedbackId): Feedback? = projection[feedbackId]

    fun list(query: FeedbackQuery): List<Feedback> {
        return projection.filter { query.feedbackIds.isEmpty() || it.key in query.feedbackIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .dropWhile { it.id >= query.afterId }
            .take(query.limit.value)
            .toList()
    }

    fun count(query: FeedbackQuery): Count {
        return Count(
            projection.filter { query.feedbackIds.isEmpty() || it.key in query.feedbackIds }
                .map { it.value }
                .reversed()
                .asSequence()
                .dropWhile { it.id >= query.afterId }
                .count()
                .toLong()
        )
    }

    fun clear() {
        projection.clear()
    }
}