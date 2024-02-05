package io.hamal.api.http.controller.topic


import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test

internal class TopicCreateControllerTest : TopicBaseControllerTest() {
    @Test
    fun `Create topic`() {
        val topicId = awaitCompleted(createTopic(TopicName("flow::topics_one"))).topicId

        verifyTopicCreated(topicId)

        with(listTopics()) {
            assertThat(topics, hasSize(1))
            val topic = topics.first()
            assertThat(topic.name, equalTo(TopicName("flow::topics_one")))
        }
    }

    @Test
    fun `Tries to create topic but name already exists`() {
        awaitCompleted(createTopic(TopicName("flow::topics_one")))

        with(createTopic(TopicName("flow::topics_one"))) {
            awaitFailed(id)
        }

        with(listTopics()) {
            assertThat(topics, hasSize(1))
            val topic = topics.first()
            assertThat(topic.name, equalTo(TopicName("flow::topics_one")))
        }
    }
}

private fun TopicCreateControllerTest.verifyTopicCreated(topicId: TopicId) {
    with(topicQueryRepository.get(topicId)) {
        assertThat(id, equalTo(topicId))
        assertThat(name, equalTo(TopicName("flow::topics_one")))
    }
}
