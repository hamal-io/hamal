package io.hamal.backend.instance.web.event

import io.hamal.lib.domain.HamalError
import io.hamal.lib.domain.vo.Content
import io.hamal.lib.domain.vo.ContentType
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test

internal class AppendEventRouteIT : BaseEventRouteIT() {
    @Test
    fun `Append event`() {
        val topicResponse = createTopic(TopicName("namespace::topics_one"))
        val result = appendEvent(
            topicResponse.topicId,
            ContentType("application/json"),
            Content("""{"hamal":"rocks"}""")
        )

        awaitReqCompleted(result.id)

        with(listEvents(topicResponse.topicId)) {
            assertThat(events, hasSize(1))

            val event = events.first()
            assertThat(event.contentType, equalTo(ContentType("application/json")))
            assertThat(event.content, equalTo(Content("""{"hamal":"rocks"}""")))
        }
    }

    @Test
    fun `Append event multiple times`() {
        val topicResponse = createTopic(TopicName("namespace::topics_one"))

        val requests = IntRange(1, 10).map {
            appendEvent(
                topicResponse.topicId,
                ContentType("application/json"),
                Content("""{"hamal":"rocks"}""")
            )
        }

        requests.forEach { awaitReqCompleted(it.id) }

        with(listEvents(topicResponse.topicId)) {
            assertThat(events, hasSize(10))
            events.forEach { event ->
                assertThat(event.contentType, equalTo(ContentType("application/json")))
                assertThat(event.content, equalTo(Content("""{"hamal":"rocks"}""")))
            }
        }
    }

    @Test
    fun `Tries to append to topic which does not exists`() {
        val topicResponse = httpTemplate.post("/v1/topics/1234/events")
            .body("text/plain", "Some Text nobody will receive".toByteArray())
            .execute()

        assertThat(topicResponse.statusCode, equalTo(HttpStatusCode.NotFound))
        require(topicResponse is ErrorHttpResponse) { "request was successful" }

        val error = topicResponse.error(HamalError::class)
        assertThat(error.message, equalTo("Topic not found"))

        verifyNoRequests()
    }
}