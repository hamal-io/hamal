package io.hamal.api.http.controller.feedback

import io.hamal.lib.domain._enum.FeedbackMood
import io.hamal.lib.domain.request.FeedbackCreateRequest
import io.hamal.lib.domain.vo.FeedbackMessage
import io.hamal.repository.api.FeedbackList
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

internal class FeedbackListControllerTest : FeedbackBaseControllerTest() {

    @Test
    fun `No feedbacks`() {
        val res = httpTemplate.get("/v1/feedbacks").execute(FeedbackList::class)
        assertThat(res.feedbacks, empty())
    }

    @Test
    fun `Single Feedback`() {
        createFeedback(
            FeedbackCreateRequest(
                mood = FeedbackMood.Normal,
                message = FeedbackMessage("My mood is so normal"),
                accountId = null
            )
        ).feedbackId

        val list = httpTemplate.get("/v1/feedbacks")
            .execute(FeedbackList::class)

        with(list.feedbacks[0]) {
            assertThat(mood, equalTo(FeedbackMood.Normal))
            assertThat(message, equalTo(FeedbackMessage("My mood is so normal")))
            assertThat(accountId, nullValue())
        }
    }

    @Test
    fun `Limit and skip feedbacks`() {
        val requests = IntRange(0, 20).map {
            createFeedback(
                FeedbackCreateRequest(
                    mood = FeedbackMood.Normal,
                    message = FeedbackMessage("This is message number $it"),
                    accountId = null
                )
            )
        }

        val ten = requests[10]

        val list = httpTemplate.get("/v1/feedbacks")
            .parameter("after_id", ten.feedbackId)
            .parameter("limit", 1)
            .execute(FeedbackList::class)

        assertThat(list.feedbacks, hasSize(1))
        with(list.feedbacks.first()) {
            assertThat(mood, equalTo(FeedbackMood.Normal))
            assertThat(message, equalTo(FeedbackMessage("This is message number 9")))
            assertThat(accountId, nullValue())
        }
    }
}