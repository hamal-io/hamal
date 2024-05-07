package io.hamal.api.http.controller.feedback

import io.hamal.lib.domain._enum.FeedbackMoods.Normal
import io.hamal.lib.domain.request.FeedbackCreateRequest
import io.hamal.lib.domain.vo.FeedbackMessage.Companion.FeedbackMessage
import io.hamal.lib.domain.vo.FeedbackMood.Companion.FeedbackMood
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

internal class FeedbackCreateControllerTest : FeedbackBaseControllerTest() {

    @Test
    fun `Creates feedback`() {
        val res = createFeedback(
            FeedbackCreateRequest(
                mood = FeedbackMood(Normal),
                message = FeedbackMessage("My mood is so normal"),
                accountId = null
            )
        )
        awaitCompleted(res.requestId)

        with(feedbackQueryRepository.get(res.id)) {
            assertThat(mood, equalTo(Normal))
            assertThat(message, equalTo(FeedbackMessage("My mood is so normal")))
            assertThat(accountId, nullValue())
        }
    }
}