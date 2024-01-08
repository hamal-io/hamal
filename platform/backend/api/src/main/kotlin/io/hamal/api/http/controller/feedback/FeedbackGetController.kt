package io.hamal.api.http.controller.feedback

import io.hamal.core.adapter.FeedbackGetPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.FeedbackId
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class FeedbackGetController(
    private val retry: Retry,
    private val getFeedback: FeedbackGetPort
) {

    @GetMapping("/v1/feedback/{fbId}")
    fun getFeedback(@PathVariable("fbId") feedbackId: FeedbackId) = retry {
        getFeedback(feedbackId) {
            ResponseEntity.ok(it)
        }
    }

}