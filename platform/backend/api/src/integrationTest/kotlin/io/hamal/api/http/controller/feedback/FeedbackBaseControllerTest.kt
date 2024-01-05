package io.hamal.api.http.controller.feedback

import io.hamal.api.http.controller.BaseControllerTest
import io.hamal.lib.domain.request.FeedbackCreateRequest
import io.hamal.lib.domain.request.FeedbackCreateRequested
import io.hamal.lib.domain.vo.FeedbackId
import io.hamal.lib.http.HttpStatusCode.Ok
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.repository.api.Feedback
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat


internal sealed class FeedbackBaseControllerTest : BaseControllerTest() {
    fun createFeedback(req: FeedbackCreateRequest): FeedbackCreateRequested {
        val createResponse = httpTemplate.post("/v1/feedback/create")
            .body(req)
            .execute()

        assertThat(createResponse.statusCode, equalTo(Ok))
        require(createResponse is HttpSuccessResponse) { "request was not successful" }
        return createResponse.result(FeedbackCreateRequested::class)
    }

    fun getFeedback(feedbackId: FeedbackId): Feedback {
        val getResponse = httpTemplate.get("/v1/feedback/{fbId}")
            .path("fbId", feedbackId)
            .execute()

        assertThat(getResponse.statusCode, equalTo(Ok))
        require(getResponse is HttpSuccessResponse) { "request was not successful" }
        return getResponse.result(Feedback::class)
    }
}