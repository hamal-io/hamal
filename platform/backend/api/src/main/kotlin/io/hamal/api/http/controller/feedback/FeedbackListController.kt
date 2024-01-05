package io.hamal.api.http.controller.feedback

import io.hamal.core.adapter.FeedbackListPort
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.FeedbackId
import io.hamal.repository.api.Feedback
import io.hamal.repository.api.FeedbackQueryRepository.FeedbackQuery
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class FeedbackListController(
    private val listFeedback: FeedbackListPort
) {
    @GetMapping("/v1/feedbacks")
    fun listFeedback(
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: FeedbackId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit,
        @RequestParam(required = false, name = "feedback_ids", defaultValue = "") feedbackIds: List<FeedbackId>,
    ): ResponseEntity<List<Feedback>> {
        return listFeedback(
            FeedbackQuery(
                afterId = afterId,
                limit = limit,
                feedbackIds = feedbackIds
            )
        ) {
            ResponseEntity.ok(it)
        }


    }
}