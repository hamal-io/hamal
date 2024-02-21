package io.hamal.api.http.controller.feedback

import io.hamal.core.adapter.FeedbackGetPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.FeedbackId
import io.hamal.lib.sdk.api.ApiFeedback
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class FeedbackGetController(
    private val retry: Retry,
    private val getFeedback: FeedbackGetPort
) {

    @GetMapping("/v1/feedback/{feedbackId}")
    fun get(@PathVariable("feedbackId") feedbackId: FeedbackId): ResponseEntity<ApiFeedback> = retry {
        getFeedback(feedbackId).let {
            ResponseEntity.ok(
                ApiFeedback(
                    id = it.id,
                    mood = it.mood,
                    message = it.message
                )
            )
        }
    }

}