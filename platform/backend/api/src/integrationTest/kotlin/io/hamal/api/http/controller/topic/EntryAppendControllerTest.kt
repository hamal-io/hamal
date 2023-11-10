package io.hamal.api.http.controller.topic

import io.hamal.lib.domain.vo.TopicEntryPayload
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.http.body
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.api.ApiError
import io.hamal.repository.api.submitted_req.TriggerCreateSubmitted
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test

internal class EntryAppendControllerTest : TopicBaseControllerTest() {

    @Test
    fun `Append entry`() {
        val topicId = awaitCompleted(createTopic(TopicName("flow::topics_one"))).topicId

        awaitCompleted(
            appendToTopic(
                topicId,
                TopicEntryPayload(MapType(mutableMapOf("hamal" to StringType("rocks"))))
            )
        )

        with(listTopicEntries(topicId)) {
            assertThat(entries, hasSize(1))

            val entry = entries.first()
            assertThat(
                entry.payload, equalTo(
                    TopicEntryPayload(MapType(mutableMapOf("hamal" to StringType("rocks"))))
                )
            )
        }
    }

    @Test
    fun `Append entry multiple times`() {
        val topicId = awaitCompleted(createTopic(TopicName("flow::topics_one"))).topicId

        awaitCompleted(
            IntRange(1, 10).map {
                appendToTopic(
                    topicId,
                    TopicEntryPayload(MapType(mutableMapOf("hamal" to StringType("rocks"))))
                )
            }
        )

        with(listTopicEntries(topicId)) {
            assertThat(entries, hasSize(10))
            entries.forEach { entry ->
                assertThat(
                    entry.payload,
                    equalTo(TopicEntryPayload(MapType(mutableMapOf("hamal" to StringType("rocks")))))
                )
            }
        }
    }

    @Test
    fun `Tries to append to topic which does not exists`() {
        val topicResponse = httpTemplate.post("/v1/topics/1234/entries")
            .body(TopicEntryPayload(MapType(mutableMapOf("hamal" to StringType("rocks")))))
            .execute()

        assertThat(topicResponse.statusCode, equalTo(NotFound))
        require(topicResponse is HttpErrorResponse) { "request was successful" }

        val error = topicResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Topic not found"))

        verifyNoRequests(TriggerCreateSubmitted::class)
    }
}