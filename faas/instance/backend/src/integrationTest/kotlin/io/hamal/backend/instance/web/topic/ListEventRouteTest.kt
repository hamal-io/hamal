package io.hamal.backend.instance.web.topic

import io.hamal.lib.domain.vo.EventId
import io.hamal.lib.domain.vo.EventPayload
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.NumberType
import io.hamal.lib.sdk.domain.ApiError
import io.hamal.lib.sdk.domain.ListEventsResponse
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

internal class ListEventRouteTest : BaseTopicRouteTest() {
    @Test
    fun `No events`() {
        val topicId = awaitCompleted(
            createTopic(TopicName("test-topic"))
        ).id(::TopicId)

        val result = listTopicEvents(topicId)
        assertThat(result.events, empty())
    }

    @Test
    fun `Single event`() {
        val topicId = awaitCompleted(
            createTopic(TopicName("test-topic"))
        ).id(::TopicId)

        awaitCompleted(
            appendToTopic(topicId, EventPayload(MapType(mutableMapOf("counter" to NumberType(1)))))
        )

        with(listTopicEvents(topicId)) {
            assertThat(topicName, equalTo(TopicName("test-topic")))
            assertThat(events, hasSize(1))

            with(events.first()) {
                assertThat(id, equalTo(EventId(1)))
                assertThat(value, equalTo(MapType(mutableMapOf("counter" to NumberType(1)))))
            }
        }
    }

    @Test
    fun `Limit events`() {
        val topicId = awaitCompleted(
            createTopic(TopicName("test-topic")).also { awaitCompleted(it.reqId) }
        ).id(::TopicId)

        awaitCompleted(
            IntRange(1, 100).map {
                appendToTopic(topicId, EventPayload(MapType(mutableMapOf("counter" to NumberType(it)))))
            }
        )

        val listResponse = httpTemplate.get("/v1/topics/{topicId}/events")
            .path("topicId", topicId)
            .parameter("limit", 23)
            .execute(ListEventsResponse::class)

        assertThat(listResponse.events, hasSize(23))

        listResponse.events.forEachIndexed { idx, event ->
            assertThat(event.value, equalTo(MapType(mutableMapOf("counter" to NumberType(idx + 1)))))
        }
    }

    @Test
    fun `Skip and limit events`() {
        val topicId = awaitCompleted(
            createTopic(TopicName("test-topic"))
        ).id(::TopicId)

        awaitCompleted(
            IntRange(1, 100).map {
                appendToTopic(topicId, EventPayload(MapType(mutableMapOf("counter" to NumberType(it)))))
            }
        )

        val listResponse = httpTemplate.get("/v1/topics/{topicId}/events")
            .path("topicId", topicId)
            .parameter("after_id", TopicId(95))
            .parameter("limit", 1)
            .execute(ListEventsResponse::class)

        assertThat(listResponse.events, hasSize(1))

        val event = listResponse.events.first()
        assertThat(event.id, equalTo(EventId(96)))
        assertThat(event.value, equalTo(MapType(mutableMapOf("counter" to NumberType(96)))))
    }

    @Test
    fun `Does not show events of different topic`() {
        val topicId = awaitCompleted(
            createTopic(TopicName("test-topic"))
        ).id(::TopicId)

        val anotherTopicId = awaitCompleted(
            createTopic(TopicName("another-test-topic"))
        ).id(::TopicId)

        awaitCompleted(
            appendToTopic(topicId, EventPayload(MapType(mutableMapOf("counter" to NumberType(1)))))
        )

        with(listTopicEvents(anotherTopicId)) {
            assertThat(topicName, equalTo(TopicName("another-test-topic")))
            assertThat(events, empty())
        }
    }

    @Test
    fun `Tries to list events of  topic which does not exists`() {
        val topicResponse = httpTemplate.get("/v1/topics/1234/events").execute()

        assertThat(topicResponse.statusCode, equalTo(HttpStatusCode.NotFound))
        require(topicResponse is ErrorHttpResponse)

        val error = topicResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Topic not found"))
    }

}