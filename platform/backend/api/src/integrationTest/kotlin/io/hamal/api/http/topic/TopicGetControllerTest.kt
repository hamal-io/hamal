package io.hamal.api.http.topic


import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.sdk.api.ApiError
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test


internal class TopicGetControllerTest : TopicBaseControllerTest() {

    @Test
    fun `Single topic`() {
        val topicId = awaitCompleted(createTopic(TopicName("namespace::topics_one"))).topicId

        with(getTopic(topicId)) {
            assertThat(id, equalTo(topicId))
            assertThat(name, equalTo(TopicName("namespace::topics_one")))
        }
    }

    @Test
    fun `Topic does not exists`() {
        val getTopicResponse = httpTemplate.get("/v1/topics/1234").execute()
        assertThat(getTopicResponse.statusCode, equalTo(HttpStatusCode.NotFound))
        require(getTopicResponse is HttpErrorResponse) { "request was successful" }

        val error = getTopicResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Topic not found"))
    }
}
