package io.hamal.api.http.controller.topic


import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test

internal class TopicCreateControllerTest : TopicBaseControllerTest() {

    @Test
    fun `Creates group topic`() {
        val topicId = awaitCompleted(createGroupTopic(TopicName("topics_one"))).topicId

        verifyTopicCreated(topicId)

        with(listTopics()) {
            assertThat(topics, hasSize(1))
            val topic = topics.first()
            assertThat(topic.name, equalTo(TopicName("topics_one")))
        }
    }

    @Test
    fun `Tries to create group topic but name already exists`() {
        awaitCompleted(createGroupTopic(TopicName("topics_one")))

        with(createGroupTopic(TopicName("topics_one"))) {
            awaitFailed(id)
        }

        with(listTopics()) {
            assertThat(topics, hasSize(1))
            val topic = topics.first()
            assertThat(topic.name, equalTo(TopicName("topics_one")))
        }
    }

    @Test
    fun `Creates public topic`() {
        val topicId = awaitCompleted(createPublicTopic(TopicName("topics_one"))).topicId

        verifyTopicCreated(topicId)

        with(listTopics()) {
            assertThat(topics, hasSize(1))
            val topic = topics.first()
            assertThat(topic.name, equalTo(TopicName("topics_one")))
        }
    }

    @Test
    fun `Tries to create public topic but name already exists`() {
        awaitCompleted(createPublicTopic(TopicName("topics_one")))

        with(createPublicTopic(TopicName("topics_one"))) {
            awaitFailed(id)
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
