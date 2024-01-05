package io.hamal.api.http.controller.feedback

import io.hamal.core.adapter.FeedbackCreatePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.request.FeedbackCreateRequest
import io.hamal.lib.domain.request.FeedbackCreateRequested
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class FeedbackCreateController(
    private val retry: Retry,
    private val createFeedback: FeedbackCreatePort
) {
    @PostMapping("/v1/feedback/create")
    fun createFeedback(
        @RequestBody req: FeedbackCreateRequest
    ): ResponseEntity<FeedbackCreateRequested> = retry {
        createFeedback(req)
    }
}


