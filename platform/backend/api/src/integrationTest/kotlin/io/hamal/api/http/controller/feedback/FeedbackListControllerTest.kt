package io.hamal.api.http.controller.feedback

import io.hamal.lib.domain._enum.FeedbackMoods.Normal
import io.hamal.lib.domain.request.FeedbackCreateRequest
import io.hamal.lib.domain.vo.FeedbackMessage.Companion.FeedbackMessage
import io.hamal.lib.domain.vo.FeedbackMood.Companion.FeedbackMood
import io.hamal.lib.sdk.api.ApiFeedbackList
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

internal class FeedbackListControllerTest : FeedbackBaseControllerTest() {

    @Test
    fun `No feedbacks`() {
        val res = httpTemplate
            .get("/v1/feedbacks")
            .execute(ApiFeedbackList::class)

        assertThat(res.feedbacks, empty())
    }

    @Test
    fun `Single Feedback`() {
        createFeedback(
            FeedbackCreateRequest(
                mood = FeedbackMood(Normal),
                message = FeedbackMessage("My mood is so normal"),
                accountId = null
            )
        ).id

        val list = httpTemplate.get("/v1/feedbacks")
            .execute(ApiFeedbackList::class)

        with(list.feedbacks[0]) {
            assertThat(mood, equalTo(Normal))
        }
    }

    @Test
    fun `Limit and skip feedbacks`() {
        val requests = IntRange(0, 20).map {
            createFeedback(
                FeedbackCreateRequest(
                    mood = FeedbackMood(Normal),
                    message = FeedbackMessage("This is message number $it"),
                    accountId = null
                )
            )
        }

        val ten = requests[10]

        val list = httpTemplate.get("/v1/feedbacks")
            .parameter("after_id", ten.id)
            .parameter("limit", 1)
            .execute(ApiFeedbackList::class)

        assertThat(list.feedbacks, hasSize(1))
        with(list.feedbacks.first()) {
            assertThat(mood, equalTo(Normal))
        }
    }
}