package io.hamal.backend.instance.web.event


import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test

internal class CreateTopicRouteTest : BaseEventRouteTest() {
    @Test
    fun `Create topic`() {
        val result = awaitCompleted(
            createTopic(TopicName("namespace::topics_one"))
        )
        verifyTopicCreated(result.topicId)

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
            awaitFailed(id)
        }

        with(listTopics()) {
            assertThat(topics, hasSize(1))
            val topic = topics.first()
            assertThat(topic.name, equalTo(TopicName("namespace::topics_one")))
        }
    }
}

private fun CreateTopicRouteTest.verifyTopicCreated(topicId: TopicId) {
    with(eventBrokerRepository.findTopic(topicId)!!) {
        assertThat(id, equalTo(topicId))
        assertThat(name, equalTo(TopicName("namespace::topics_one")))
    }
}
