package io.hamal.api.http.controller.topic

import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.domain.vo.TopicEntryId
import io.hamal.lib.domain.vo.TopicEntryPayload
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiTopicEntryList
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

internal class EntryListControllerTest : TopicBaseControllerTest() {
    @Test
    fun `No entries`() {
        val topicId = awaitCompleted(createTopic(TopicName("test-topic"))).topicId

        val result = listTopicEntries(topicId)
        assertThat(result.entries, empty())
    }

    @Test
    fun `Single entry`() {
        val topicId = awaitCompleted(createTopic(TopicName("test-topic"))).topicId

        awaitCompleted(
            appendToTopic(topicId, TopicEntryPayload(HotObject.builder().set("counter", 1).build()))
        )

        with(listTopicEntries(topicId)) {
            assertThat(topicName, equalTo(TopicName("test-topic")))
            assertThat(entries, hasSize(1))

            with(entries.first()) {
                assertThat(id, equalTo(TopicEntryId(1)))
                assertThat(payload, equalTo(TopicEntryPayload(HotObject.builder().set("counter", 1).build())))
            }
        }
    }

    @Test
    fun `Limit entries`() {
        val topicId = awaitCompleted(createTopic(TopicName("test-topic")).also { awaitCompleted(it.id) }).topicId

        awaitCompleted(
            IntRange(1, 100).map {
                appendToTopic(topicId, TopicEntryPayload(HotObject.builder().set("counter", it).build()))
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
                    TopicEntryPayload(HotObject.builder().set("counter", idx + 1).build())
                )
            )
        }
    }

    @Test
    fun `Skip and limit entries`() {
        val topicId = awaitCompleted(createTopic(TopicName("test-topic"))).topicId

        awaitCompleted(
            IntRange(1, 100).map {
                appendToTopic(topicId, TopicEntryPayload(HotObject.builder().set("counter", it).build()))
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
        assertThat(event.payload, equalTo(TopicEntryPayload(HotObject.builder().set("counter", 96).build())))
    }

    @Test
    fun `Does not show entries of different topic`() {
        val topicId = awaitCompleted(createTopic(TopicName("test-topic"))).topicId
        val anotherTopicId = awaitCompleted(createTopic(TopicName("another-test-topic"))).topicId

        awaitCompleted(
            appendToTopic(topicId, TopicEntryPayload(HotObject.builder().set("counter", 1).build()))
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
        require(topicResponse is HttpErrorResponse)

        val error = topicResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Topic not found"))
    }

}