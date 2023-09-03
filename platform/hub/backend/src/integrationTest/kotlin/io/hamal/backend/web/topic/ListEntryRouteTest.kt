package io.hamal.backend.web.topic

import io.hamal.lib.domain.vo.TopicEntryId
import io.hamal.lib.domain.vo.TopicEntryPayload
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.NumberType
import io.hamal.lib.sdk.hub.domain.ApiError
import io.hamal.lib.sdk.hub.domain.ApiTopicEntryList
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

internal class ListEntryRouteTest : BaseTopicRouteTest() {
    @Test
    fun `No entries`() {
        val topicId = awaitCompleted(
            createTopic(TopicName("test-topic"))
        ).id(::TopicId)

        val result = listTopicEntries(topicId)
        assertThat(result.entries, empty())
    }

    @Test
    fun `Single entry`() {
        val topicId = awaitCompleted(
            createTopic(TopicName("test-topic"))
        ).id(::TopicId)

        awaitCompleted(
            appendToTopic(topicId, TopicEntryPayload(MapType(mutableMapOf("counter" to NumberType(1)))))
        )

        with(listTopicEntries(topicId)) {
            assertThat(topicName, equalTo(TopicName("test-topic")))
            assertThat(entries, hasSize(1))

            with(entries.first()) {
                assertThat(id, equalTo(TopicEntryId(1)))
                assertThat(payload, equalTo(TopicEntryPayload(MapType(mutableMapOf("counter" to NumberType(1))))))
            }
        }
    }

    @Test
    fun `Limit entries`() {
        val topicId = awaitCompleted(
            createTopic(TopicName("test-topic")).also { awaitCompleted(it.reqId) }
        ).id(::TopicId)

        awaitCompleted(
            IntRange(1, 100).map {
                appendToTopic(topicId, TopicEntryPayload(MapType(mutableMapOf("counter" to NumberType(it)))))
            }
        )

        val listResponse = httpTemplate.get("/v1/topics/{topicId}/entries")
            .path("topicId", topicId)
            .parameter("limit", 23)
            .execute(ApiTopicEntryList::class)

        assertThat(listResponse.entries, hasSize(23))

        listResponse.entries.forEachIndexed { idx, event ->
            assertThat(
                event.payload, equalTo(
                    TopicEntryPayload(MapType(mutableMapOf("counter" to NumberType(idx + 1))))
                )
            )
        }
    }

    @Test
    fun `Skip and limit entries`() {
        val topicId = awaitCompleted(
            createTopic(TopicName("test-topic"))
        ).id(::TopicId)

        awaitCompleted(
            IntRange(1, 100).map {
                appendToTopic(topicId, TopicEntryPayload(MapType(mutableMapOf("counter" to NumberType(it)))))
            }
        )

        val listResponse = httpTemplate.get("/v1/topics/{topicId}/entries")
            .path("topicId", topicId)
            .parameter("after_id", TopicId(95))
            .parameter("limit", 1)
            .execute(ApiTopicEntryList::class)

        assertThat(listResponse.entries, hasSize(1))

        val event = listResponse.entries.first()
        assertThat(event.id, equalTo(TopicEntryId(96)))
        assertThat(event.payload, equalTo(TopicEntryPayload(MapType(mutableMapOf("counter" to NumberType(96))))))
    }

    @Test
    fun `Does not show entries of different topic`() {
        val topicId = awaitCompleted(
            createTopic(TopicName("test-topic"))
        ).id(::TopicId)

        val anotherTopicId = awaitCompleted(
            createTopic(TopicName("another-test-topic"))
        ).id(::TopicId)

        awaitCompleted(
            appendToTopic(topicId, TopicEntryPayload(MapType(mutableMapOf("counter" to NumberType(1)))))
        )

        with(listTopicEntries(anotherTopicId)) {
            assertThat(topicName, equalTo(TopicName("another-test-topic")))
            assertThat(entries, empty())
        }
    }

    @Test
    fun `Tries to list entries of topic which does not exists`() {
        val topicResponse = httpTemplate.get("/v1/topics/1234/entries").execute()

        assertThat(topicResponse.statusCode, equalTo(HttpStatusCode.NotFound))
        require(topicResponse is ErrorHttpResponse)

        val error = topicResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Topic not found"))
    }

}