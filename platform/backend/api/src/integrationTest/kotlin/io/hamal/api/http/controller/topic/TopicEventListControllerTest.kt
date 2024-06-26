package io.hamal.api.http.controller.topic

import io.hamal.lib.common.value.ValueObject
import io.hamal.lib.domain.vo.TopicEventId.Companion.TopicEventId
import io.hamal.lib.domain.vo.TopicEventPayload
import io.hamal.lib.domain.vo.TopicId.Companion.TopicId
import io.hamal.lib.domain.vo.TopicName.Companion.TopicName
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiTopicEventList
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

internal class TopicEventListControllerTest : TopicBaseControllerTest() {
    @Test
    fun `No events`() {
        val topicId = awaitCompleted(createTopic(TopicName("test-topic"))).id

        val result = listTopicEvents(topicId)
        assertThat(result.events, empty())
    }

    @Test
    fun `Single event`() {
        val topicId = awaitCompleted(createTopic(TopicName("test-topic"))).id

        awaitCompleted(
            appendToTopic(topicId, TopicEventPayload(ValueObject.builder().set("counter", 1).build()))
        )

        with(listTopicEvents(topicId)) {
            assertThat(topicName, equalTo(TopicName("test-topic")))
            assertThat(events, hasSize(1))

            with(events.first()) {
                assertThat(id, equalTo(TopicEventId(1)))
                assertThat(payload, equalTo(TopicEventPayload(ValueObject.builder().set("counter", 1).build())))
            }
        }
    }

    @Test
    fun `Limit events`() {
        val topicId = awaitCompleted(createTopic(TopicName("test-topic")).also { awaitCompleted(it.requestId) }).id

        awaitCompleted(
            IntRange(1, 100).map {
                appendToTopic(topicId, TopicEventPayload(ValueObject.builder().set("counter", it).build()))
            }
        )

        val listResponse = httpTemplate.get("/v1/topics/{topicId}/events")
            .path("topicId", topicId)
            .parameter("limit", 23)
            .execute(ApiTopicEventList::class)

        assertThat(listResponse.events, hasSize(23))

        listResponse.events.forEachIndexed { idx, event ->
            assertThat(
                event.payload, equalTo(
                    TopicEventPayload(ValueObject.builder().set("counter", idx + 1).build())
                )
            )
        }
    }

    @Test
    fun `Skip and limit events`() {
        val topicId = awaitCompleted(createTopic(TopicName("test-topic"))).id

        awaitCompleted(
            IntRange(1, 100).map {
                appendToTopic(topicId, TopicEventPayload(ValueObject.builder().set("counter", it).build()))
            }
        )

        val listResponse = httpTemplate.get("/v1/topics/{topicId}/events")
            .path("topicId", topicId)
            .parameter("after_id", TopicId(95))
            .parameter("limit", 1)
            .execute(ApiTopicEventList::class)

        assertThat(listResponse.events, hasSize(1))

        val event = listResponse.events.first()
        assertThat(event.id, equalTo(TopicEventId(96)))
        assertThat(event.payload, equalTo(TopicEventPayload(ValueObject.builder().set("counter", 96).build())))
    }

    @Test
    fun `Does not show events of different topic`() {
        val topicId = awaitCompleted(createTopic(TopicName("test-topic"))).id
        val anotherTopicId = awaitCompleted(createTopic(TopicName("another-test-topic"))).id

        awaitCompleted(
            appendToTopic(topicId, TopicEventPayload(ValueObject.builder().set("counter", 1).build()))
        )

        with(listTopicEvents(anotherTopicId)) {
            assertThat(topicName, equalTo(TopicName("another-test-topic")))
            assertThat(events, empty())
        }
    }

    @Test
    fun `Tries to list events of topic which does not exists`() {
        val topicResponse = httpTemplate.get("/v1/topics/1234/events").execute()

        assertThat(topicResponse.statusCode, equalTo(HttpStatusCode.NotFound))
        require(topicResponse is HttpErrorResponse)

        val error = topicResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Topic not found"))
    }

}