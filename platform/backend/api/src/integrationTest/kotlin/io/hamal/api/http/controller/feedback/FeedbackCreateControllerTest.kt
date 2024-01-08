package io.hamal.api.http.controller.feedback

import io.hamal.lib.domain._enum.FeedbackMood
import io.hamal.lib.domain.request.FeedbackCreateRequest
import io.hamal.lib.domain.vo.FeedbackMessage
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

internal class FeedbackCreateControllerTest : FeedbackBaseControllerTest() {

    @Test
    fun `Creates feedback`() {
        val res = createFeedback(
            FeedbackCreateRequest(
                mood = FeedbackMood.Normal,
                message = FeedbackMessage("My mood is so normal"),
                accountId = null
            )
        )

        with(feedbackQueryRepository.get(res.feedbackId)) {
            assertThat(mood, equalTo(FeedbackMood.Normal))
            assertThat(message, equalTo(FeedbackMessage("My mood is so normal")))
            assertThat(accountId, nullValue())
        }
    }
}