package io.hamal.backend.instance.web.event

import io.hamal.lib.domain.HamalError
import io.hamal.lib.domain.req.SubmittedCreateTriggerReq
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.body
import io.hamal.lib.script.api.value.StringValue
import io.hamal.lib.script.api.value.TableValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test

internal class AppendEventRouteTest : BaseEventRouteTest() {
    @Test
    fun `Append event`() {
        val topicResponse = awaitCompleted(
            createTopic(TopicName("namespace::topics_one"))
        )

        awaitCompleted(
            appendEvent(
                topicResponse.topicId,
                TableValue("hamal" to StringValue("rocks"))
            )
        )

        with(listEvents(topicResponse.topicId)) {
            assertThat(events, hasSize(1))

            val event = events.first()
            assertThat(event.value, equalTo(TableValue("hamal" to StringValue("rocks"))))
        }
    }

    @Test
    fun `Append event multiple times`() {
        val topicResponse = awaitCompleted(
            createTopic(TopicName("namespace::topics_one"))
        )

        awaitCompleted(
            IntRange(1, 10).map {
                appendEvent(
                    topicResponse.topicId,
                    TableValue("hamal" to StringValue("rocks"))
                )
            }
        )

        with(listEvents(topicResponse.topicId)) {
            assertThat(events, hasSize(10))
            events.forEach { event ->
                assertThat(event.value, equalTo(TableValue("hamal" to StringValue("rocks"))))
            }
        }
    }

    @Test
    fun `Tries to append to topic which does not exists`() {
        val topicResponse = httpTemplate.post("/v1/topics/1234/events")
            .body(TableValue("hamal" to StringValue("rocks")))
            .execute()

        assertThat(topicResponse.statusCode, equalTo(HttpStatusCode.NotFound))
        require(topicResponse is ErrorHttpResponse) { "request was successful" }

        val error = topicResponse.error(HamalError::class)
        assertThat(error.message, equalTo("Topic not found"))

        verifyNoRequests(SubmittedCreateTriggerReq::class)
    }
}