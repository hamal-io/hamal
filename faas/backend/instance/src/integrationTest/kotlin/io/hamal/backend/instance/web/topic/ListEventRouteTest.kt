package io.hamal.backend.instance.web.topic

import io.hamal.lib.domain.HamalError
import io.hamal.lib.domain.vo.EventId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.kua.value.NumberValue
import io.hamal.lib.kua.value.TableValue
import io.hamal.lib.sdk.domain.ListEventsResponse
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

internal class ListEventRouteTest : BaseTopicRouteTest() {
    @Test
    fun `No events`() {
        val topicResponse = awaitCompleted(
            createTopic(TopicName("test-topic"))
        )

        val result = listTopicEvents(topicResponse.id)
        assertThat(result.events, empty())
    }

    @Test
    fun `Single event`() {
        val topicResponse = awaitCompleted(
            createTopic(TopicName("test-topic"))
        )

        awaitCompleted(
            appendEvent(topicResponse.id, TableValue("counter" to NumberValue(1)))
        )

        with(listTopicEvents(topicResponse.id)) {
            assertThat(topicId, equalTo(topicResponse.id))
            assertThat(topicName, equalTo(TopicName("test-topic")))
            assertThat(events, hasSize(1))

            with(events.first()) {
                assertThat(id, equalTo(EventId(1)))
                assertThat(value, equalTo(TableValue("counter" to NumberValue(1))))
            }
        }
    }

    @Test
    fun `Limit events`() {
        val topicResponse = awaitCompleted(
            createTopic(TopicName("test-topic")).also { awaitCompleted(it.reqId) }
        )

        awaitCompleted(
            IntRange(1, 100).map {
                appendEvent(topicResponse.id, TableValue("counter" to NumberValue(it)))
            }
        )

        val listResponse = httpTemplate.get("/v1/topics/${topicResponse.id.value}/events")
            .parameter("limit", 23)
            .execute(ListEventsResponse::class)

        assertThat(listResponse.events, hasSize(23))

        listResponse.events.forEachIndexed { idx, event ->
            assertThat(event.value, equalTo(TableValue("counter" to NumberValue(idx + 1))))
        }
    }

    @Test
    fun `Skip and limit events`() {
        val topicResponse = awaitCompleted(
            createTopic(TopicName("test-topic"))
        )

        awaitCompleted(
            IntRange(1, 100).map {
                appendEvent(topicResponse.id, TableValue("counter" to NumberValue(it)))
            }
        )

        val listResponse = httpTemplate.get("/v1/topics/${topicResponse.id.value}/events")
            .parameter("after_id", 95)
            .parameter("limit", 1)
            .execute(ListEventsResponse::class)

        assertThat(listResponse.events, hasSize(1))

        val event = listResponse.events.first()
        assertThat(event.id, equalTo(EventId(96)))
        assertThat(event.value, equalTo(TableValue("counter" to NumberValue(96))))
    }

    @Test
    fun `Does not show events of different topic`() {
        val topicResponse = awaitCompleted(
            createTopic(TopicName("test-topic"))
        )

        val anotherTopicResponse = awaitCompleted(
            createTopic(TopicName("another-test-topic"))
        )

        awaitCompleted(
            appendEvent(topicResponse.id, TableValue("counter" to NumberValue(1)))
        )

        with(listTopicEvents(anotherTopicResponse.id)) {
            assertThat(topicId, equalTo(anotherTopicResponse.id))
            assertThat(topicName, equalTo(TopicName("another-test-topic")))
            assertThat(events, empty())
        }
    }

    @Test
    fun `Tries to list events of  topic which does not exists`() {
        val topicResponse = httpTemplate.get("/v1/topics/1234/events").execute()

        assertThat(topicResponse.statusCode, equalTo(HttpStatusCode.NotFound))
        require(topicResponse is ErrorHttpResponse)

        val error = topicResponse.error(HamalError::class)
        assertThat(error.message, equalTo("Topic not found"))
    }

}