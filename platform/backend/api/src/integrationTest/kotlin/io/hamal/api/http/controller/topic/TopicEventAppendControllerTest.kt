package io.hamal.api.http.controller.topic

import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.domain.request.TriggerCreateRequested
import io.hamal.lib.domain.vo.TopicEventPayload
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiError
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test

internal class TopicEventAppendControllerTest : TopicBaseControllerTest() {

    @Test
    fun `Append event`() {
        val topicId = awaitCompleted(createGroupTopic(TopicName("topics_one"))).topicId

        awaitCompleted(
            appendToTopic(
                topicId,
                TopicEventPayload(HotObject.builder().set("hamal", "rocks").build())
            )
        )

        with(listTopicEvents(topicId)) {
            assertThat(events, hasSize(1))

            val entry = events.first()
            assertThat(
                entry.payload, equalTo(
                    TopicEventPayload(HotObject.builder().set("hamal", "rocks").build())
                )
            )
        }
    }

    @Test
    fun `Append event multiple times`() {
        val topicId = awaitCompleted(createGroupTopic(TopicName("topics_one"))).topicId

        awaitCompleted(
            IntRange(1, 10).map {
                appendToTopic(
                    topicId,
                    TopicEventPayload(HotObject.builder().set("hamal", "rocks").build())
                )
            }
        )

        with(listTopicEvents(topicId)) {
            assertThat(events, hasSize(10))
            events.forEach { entry ->
                assertThat(
                    entry.payload,
                    equalTo(TopicEventPayload(HotObject.builder().set("hamal", "rocks").build()))
                )
            }
        }
    }

    @Test
    fun `Tries to append event to topic which does not exists`() {
        val topicResponse = httpTemplate.post("/v1/topics/1234/events")
            .body(TopicEventPayload(HotObject.builder().set("hamal", "rocks").build()))
            .execute()

        assertThat(topicResponse.statusCode, equalTo(NotFound))
        require(topicResponse is HttpErrorResponse) { "request was successful" }

        val error = topicResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Topic not found"))

        verifyNoRequests(TriggerCreateRequested::class)
    }
}