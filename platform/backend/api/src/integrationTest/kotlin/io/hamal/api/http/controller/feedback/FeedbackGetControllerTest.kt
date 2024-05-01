package io.hamal.api.http.controller.feedback

import io.hamal.lib.domain._enum.FeedbackMood
import io.hamal.lib.domain.request.FeedbackCreateRequest
import io.hamal.lib.domain.vo.FeedbackMessage.Companion.FeedbackMessage
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.sdk.api.ApiError
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

internal class FeedbackGetControllerTest : FeedbackBaseControllerTest() {

    @Test
    fun `Get feedback`() {
        val res = createFeedback(
            FeedbackCreateRequest(
                mood = FeedbackMood.Normal,
                message = FeedbackMessage("My mood is so normal"),
                accountId = null
            )
        )

        with(getFeedback(res.id)) {
            assertThat(id, equalTo(res.id))
            assertThat(mood, equalTo(FeedbackMood.Normal))
            assertThat(message, equalTo(FeedbackMessage("My mood is so normal")))
            assertThat(accountId, nullValue())
        }
    }

    @Test
    fun `Tries to get feedback that does not exist`() {
        val res = httpTemplate.get("/v1/feedback/3333333").execute()
        assertThat(res.statusCode, equalTo(NotFound))
        require(res is HttpErrorResponse) { "request was successful" }

        val error = res.error(ApiError::class)
        assertThat(error.message, equalTo("Feedback not found"))
    }
}