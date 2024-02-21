package io.hamal.api.http.controller.feedback

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.FeedbackCreatePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.request.FeedbackCreateRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class FeedbackCreateController(
    private val retry: Retry,
    private val createFeedback: FeedbackCreatePort
) {
    @PostMapping("/v1/feedback")
    fun create(
        @RequestBody req: FeedbackCreateRequest
    ) = retry {
        createFeedback(req).accepted()
    }
}


