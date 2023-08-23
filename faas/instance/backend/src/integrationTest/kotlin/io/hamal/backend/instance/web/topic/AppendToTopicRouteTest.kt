package io.hamal.backend.instance.web.topic

import io.hamal.backend.repository.api.submitted_req.SubmittedCreateTriggerReq
import io.hamal.lib.domain.vo.EventPayload
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.http.body
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.domain.ApiError
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test

internal class AppendToTopicRouteTest : BaseTopicRouteTest() {
    @Test
    fun `Append event`() {
        val topicId = awaitCompleted(
            createTopic(TopicName("namespace::topics_one"))
        ).id(::TopicId)

        awaitCompleted(
            appendToTopic(
                topicId,
                EventPayload(MapType(mutableMapOf("hamal" to StringType("rocks"))))
            )
        )

        with(listTopicEvents(topicId)) {
            assertThat(events, hasSize(1))

            val event = events.first()
            assertThat(event.value, equalTo(MapType(mutableMapOf("hamal" to StringType("rocks")))))
        }
    }

    @Test
    fun `Append event multiple times`() {
        val topicId = awaitCompleted(
            createTopic(TopicName("namespace::topics_one"))
        ).id(::TopicId)

        awaitCompleted(
            IntRange(1, 10).map {
                appendToTopic(
                    topicId,
                    EventPayload(MapType(mutableMapOf("hamal" to StringType("rocks"))))
                )
            }
        )

        with(listTopicEvents(topicId)) {
            assertThat(events, hasSize(10))
            events.forEach { event ->
                assertThat(event.value, equalTo(MapType(mutableMapOf("hamal" to StringType("rocks")))))
            }
        }
    }

    @Test
    fun `Tries to append to topic which does not exists`() {
        val topicResponse = httpTemplate.post("/v1/topics/1234/events")
            .body(EventPayload(MapType(mutableMapOf("hamal" to StringType("rocks")))))
            .execute()

        assertThat(topicResponse.statusCode, equalTo(NotFound))
        require(topicResponse is ErrorHttpResponse) { "request was successful" }

        val error = topicResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Topic not found"))

        verifyNoRequests(SubmittedCreateTriggerReq::class)
    }
}