package io.hamal.api.http.topic


import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test

internal class TopicCreateControllerTest : TopicBaseControllerTest() {
    @Test
    fun `Create topic`() {
        val topicId = awaitCompleted(
            createTopic(TopicName("namespace::topics_one"))
        ).id(::TopicId)
        verifyTopicCreated(topicId)

        with(listTopics()) {
            assertThat(topics, hasSize(1))
            val topic = topics.first()
            assertThat(topic.name, equalTo(TopicName("namespace::topics_one")))
        }
    }

    @Test
    fun `Tries to create topic but name already exists`() {
        awaitCompleted(createTopic(TopicName("namespace::topics_one")))

        with(createTopic(TopicName("namespace::topics_one"))) {
            awaitFailed(reqId)
        }

        with(listTopics()) {
            assertThat(topics, hasSize(1))
            val topic = topics.first()
            assertThat(topic.name, equalTo(TopicName("namespace::topics_one")))
        }
    }
}

private fun TopicCreateControllerTest.verifyTopicCreated(topicId: TopicId) {
    with(eventBrokerRepository.findTopic(topicId)!!) {
        assertThat(id, equalTo(topicId))
        assertThat(name, equalTo(TopicName("namespace::topics_one")))
    }
}
