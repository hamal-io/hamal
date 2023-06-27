package io.hamal.backend.instance.web.event

import io.hamal.lib.domain.HamalError
import io.hamal.lib.domain.vo.Content
import io.hamal.lib.domain.vo.ContentType
import io.hamal.lib.domain.vo.EventId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.sdk.domain.ListEventsResponse
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

internal class ListEventsRouteIT : BaseEventRouteIT() {
    @Test
    fun `No events`() {
        val topicResponse = createTopic(TopicName("test-topic"))
        awaitReqCompleted(topicResponse.id)

        val result = listEvents(topicResponse.topicId)
        assertThat(result.events, empty())
    }

    @Test
    fun `Single event`() {
        val topicResponse = createTopic(TopicName("test-topic"))
        val appendResponse = appendEvent(topicResponse.topicId, ContentType("text/plain"), Content("1".toByteArray()))
        awaitReqCompleted(appendResponse.id)

        with(listEvents(topicResponse.topicId)) {
            assertThat(topicId, equalTo(topicResponse.topicId))
            assertThat(topicName, equalTo(TopicName("test-topic")))
            assertThat(events, hasSize(1))

            with(events.first()) {
                assertThat(id, equalTo(EventId(1)))
                assertThat(contentType, equalTo(ContentType("text/plain")))
                assertThat(content, equalTo(Content("1")))
            }
        }
    }

    @Test
    fun `Limit events`() {
        val topicResponse = createTopic(TopicName("test-topic")).also { awaitReqCompleted(it.id) }
        val requests = IntRange(1, 100).map {
            appendEvent(topicResponse.topicId, ContentType("text/plain"), Content(it.toString().toByteArray()))
        }

        requests.forEach { awaitReqCompleted(it.id) }

        val listResponse = httpTemplate.get("/v1/topics/${topicResponse.topicId.value}/events")
            .parameter("limit", 23)
            .execute(ListEventsResponse::class)

        assertThat(listResponse.events, hasSize(23))

        listResponse.events.forEachIndexed { idx, event ->
            assertThat(event.content, equalTo(Content((idx + 1).toString())))
        }
    }

    @Test
    fun `Skip and limit events`() {
        val topicResponse = createTopic(TopicName("test-topic"))
        val requests = IntRange(1, 100).map {
            appendEvent(topicResponse.topicId, ContentType("text/plain"), Content(it.toString().toByteArray()))
        }

        requests.forEach { awaitReqCompleted(it.id) }

        val listResponse = httpTemplate.get("/v1/topics/${topicResponse.topicId.value}/events")
            .parameter("after_id", 95)
            .parameter("limit", 1)
            .execute(ListEventsResponse::class)

        assertThat(listResponse.events, hasSize(1))

        val event = listResponse.events.first()
        assertThat(event.id, equalTo(EventId(96)))
        assertThat(event.content, equalTo(Content((96).toString())))
    }

    @Test
    fun `Does not show events of different topic`() {
        val topicResponse = createTopic(TopicName("test-topic"))
        val anotherTopicResponse = createTopic(TopicName("another-test-topic"))
        appendEvent(topicResponse.topicId, ContentType("text/plain"), Content("1".toByteArray()))
            .also { awaitReqCompleted(it.id) }

        with(listEvents(anotherTopicResponse.topicId)) {
            assertThat(topicId, equalTo(anotherTopicResponse.topicId))
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