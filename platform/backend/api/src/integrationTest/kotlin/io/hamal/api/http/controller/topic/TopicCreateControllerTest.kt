package io.hamal.api.http.controller.topic


import io.hamal.lib.domain._enum.TopicType
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName.Companion.TopicName
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiTopicCreateRequest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test

internal class TopicCreateControllerTest : TopicBaseControllerTest() {

    @Test
    fun `Tries to create internal topic`() {
        val createTopicResponse = httpTemplate.post("/v1/namespaces/{namespaceId}/topics")
            .path("namespaceId", testNamespace.id)
            .body(ApiTopicCreateRequest(TopicName("topics_one"), TopicType.Internal))
            .execute()

        assertThat(createTopicResponse.statusCode, equalTo(HttpStatusCode.BadRequest))
        require(createTopicResponse is HttpErrorResponse) { "request was successful" }

        createTopicResponse.error(ApiError::class).also { error ->
            assertThat(error.message, equalTo("Can not append internal topics"))
        }

        with(listTopics()) {
            assertThat(topics, hasSize(0))
        }
    }


    @Test
    fun `Creates namespace topic`() {
        val topicId = awaitCompleted(createTopic(TopicName("topics_one"), TopicType.Namespace)).id

        verifyTopicCreated(topicId)

        with(listTopics()) {
            assertThat(topics, hasSize(1))
            val topic = topics.first()
            assertThat(topic.name, equalTo(TopicName("topics_one")))
        }
    }

    @Test
    fun `Tries to create namespace topic but name already exists`() {
        awaitCompleted(createTopic(TopicName("topics_one")))

        with(createTopic(TopicName("topics_one"), TopicType.Namespace)) {
            awaitFailed(requestId)
        }

        with(listTopics()) {
            assertThat(topics, hasSize(1))
            val topic = topics.first()
            assertThat(topic.name, equalTo(TopicName("topics_one")))
        }
    }


    @Test
    fun `Creates workspace topic`() {
        val topicId = awaitCompleted(createTopic(TopicName("topics_one"), TopicType.Workspace)).id

        verifyTopicCreated(topicId)

        with(listTopics()) {
            assertThat(topics, hasSize(1))
            val topic = topics.first()
            assertThat(topic.name, equalTo(TopicName("topics_one")))
        }
    }

    @Test
    fun `Tries to create workspace topic but name already exists`() {
        awaitCompleted(createTopic(TopicName("topics_one")))

        with(createTopic(TopicName("topics_one"), TopicType.Workspace)) {
            awaitFailed(requestId)
        }

        with(listTopics()) {
            assertThat(topics, hasSize(1))
            val topic = topics.first()
            assertThat(topic.name, equalTo(TopicName("topics_one")))
        }
    }

    @Test
    fun `Creates public topic`() {
        val topicId = awaitCompleted(createTopic(TopicName("topics_one"), TopicType.Public)).id

        verifyTopicCreated(topicId)

        with(listTopics()) {
            assertThat(topics, hasSize(1))
            val topic = topics.first()
            assertThat(topic.name, equalTo(TopicName("topics_one")))
        }
    }

    @Test
    fun `Tries to create public topic but name already exists`() {
        awaitCompleted(createTopic(TopicName("topics_one")))

        with(createTopic(TopicName("topics_one"), TopicType.Public)) {
            awaitFailed(requestId)
        }

        with(listTopics()) {
            assertThat(topics, hasSize(1))
            val topic = topics.first()
            assertThat(topic.name, equalTo(TopicName("topics_one")))
        }
    }

}


private fun TopicCreateControllerTest.verifyTopicCreated(topicId: TopicId) {
    with(topicQueryRepository.get(topicId)) {
        assertThat(id, equalTo(topicId))
        assertThat(name, equalTo(TopicName("topics_one")))
    }
}
