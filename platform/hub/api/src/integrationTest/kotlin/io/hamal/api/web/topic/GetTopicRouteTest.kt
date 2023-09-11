package io.hamal.api.web.topic


import io.hamal.lib.sdk.hub.HubError
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test


internal class GetTopicRouteTest : BaseTopicRouteTest() {

    @Test
    fun `Single topic`() {
        val topicId = awaitCompleted(createTopic(TopicName("namespace::topics_one"))).id(::TopicId)

        with(getTopic(topicId)) {
            assertThat(id, equalTo(topicId))
            assertThat(name, equalTo(TopicName("namespace::topics_one")))
        }
    }

    @Test
    fun `Topic does not exists`() {
        val getTopicResponse = httpTemplate.get("/v1/topics/1234").execute()
        assertThat(getTopicResponse.statusCode, equalTo(HttpStatusCode.NotFound))
        require(getTopicResponse is ErrorHttpResponse) { "request was successful" }

        val error = getTopicResponse.error(HubError::class)
        assertThat(error.message, equalTo("Topic not found"))
    }
}