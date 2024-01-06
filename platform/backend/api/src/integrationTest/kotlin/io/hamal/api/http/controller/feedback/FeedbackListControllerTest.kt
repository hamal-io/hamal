package io.hamal.api.http.controller.feedback

import io.hamal.repository.api.FeedbackList
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.empty
import org.junit.jupiter.api.Test

internal class FeedbackListControllerTest : FeedbackBaseControllerTest() {
    @Test
    fun `No feedbacks`() {
        val res = httpTemplate.get("/v1/feedbacks")
            .execute(FeedbackList::class)

        assertThat(res.feedbacks, empty())
    }
}