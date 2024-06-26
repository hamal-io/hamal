package io.hamal.api.http.controller.feedback

import io.hamal.core.adapter.feedback.FeedbackListPort
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.FeedbackId
import io.hamal.lib.sdk.api.ApiFeedbackList
import io.hamal.lib.sdk.api.ApiFeedbackList.Feedback
import io.hamal.repository.api.FeedbackQueryRepository.FeedbackQuery
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class FeedbackListController(
    private val feedbackList: FeedbackListPort
) {
    @GetMapping("/v1/feedbacks")
    fun listFeedback(
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: FeedbackId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit,
        @RequestParam(required = false, name = "feedback_ids", defaultValue = "") feedbackIds: List<FeedbackId>,
    ): ResponseEntity<ApiFeedbackList> {
        return feedbackList(
            FeedbackQuery(
                afterId = afterId,
                limit = limit,
                feedbackIds = feedbackIds
            )
        ).let {
            ResponseEntity.ok(
                ApiFeedbackList(
                    feedbacks = it.map {
                        Feedback(
                            id = it.id,
                            mood = it.mood
                        )
                    })
            )
        }
    }
}