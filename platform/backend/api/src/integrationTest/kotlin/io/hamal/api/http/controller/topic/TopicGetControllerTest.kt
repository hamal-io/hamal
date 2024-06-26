package io.hamal.api.http.controller.topic


import io.hamal.lib.domain._enum.TopicTypes.*
import io.hamal.lib.domain.vo.TopicName.Companion.TopicName
import io.hamal.lib.domain.vo.TopicType.Companion.TopicType
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.sdk.api.ApiError
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test


internal class TopicGetControllerTest : TopicBaseControllerTest() {

    @Test
    fun `Gets namespace topic`() {
        val topicId = awaitCompleted(createTopic(TopicName("topics_one"), TopicType(Namespace))).id
        with(getTopic(topicId)) {
            assertThat(id, equalTo(topicId))
            assertThat(name, equalTo(TopicName("topics_one")))
            assertThat(type, equalTo(Namespace))
        }
    }

    @Test
    fun `Gets workspace topic`() {
        val topicId = awaitCompleted(createTopic(TopicName("topics_one"), TopicType(Workspace))).id
        with(getTopic(topicId)) {
            assertThat(id, equalTo(topicId))
            assertThat(name, equalTo(TopicName("topics_one")))
            assertThat(type, equalTo(Workspace))
        }
    }

    @Test
    fun `Gets public topic`() {
        val topicId = awaitCompleted(createTopic(TopicName("topics_one"), TopicType(Public))).id

        with(getTopic(topicId)) {
            assertThat(id, equalTo(topicId))
            assertThat(name, equalTo(TopicName("topics_one")))
            assertThat(type, equalTo(Public))
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
