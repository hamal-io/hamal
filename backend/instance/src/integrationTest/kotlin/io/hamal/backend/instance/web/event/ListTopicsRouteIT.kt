package io.hamal.backend.instance.web.event


import io.hamal.lib.domain.vo.TopicName
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test


internal class ListTopicsRouteIT : BaseEventRouteIT() {
    @Test
    fun `No topics`() {
        val result = listTopics()
        assertThat(result.topics, empty())
    }

    @Test
    fun `Single topic`() {
        createTopic(TopicName("namespace::topics_one"))
        Thread.sleep(10)

        val result = listTopics()
        assertThat(result.topics, hasSize(1))

        val topic = result.topics.first()
        assertThat(topic.name, equalTo(TopicName("namespace::topics_one")))
    }

    @Test
    fun `Multiple topics`() {
        createTopic(TopicName("namespace::topics_one"))
        createTopic(TopicName("namespace::topics_two"))
        createTopic(TopicName("namespace::topics_three"))
        Thread.sleep(10)

        val result = listTopics()
        assertThat(result.topics, hasSize(3))

        val topicOne = result.topics.first()
        assertThat(topicOne.name, equalTo(TopicName("namespace::topics_one")))

        val topicTwo = result.topics[1]
        assertThat(topicTwo.name, equalTo(TopicName("namespace::topics_two")))

        val topicThree = result.topics[2]
        assertThat(topicThree.name, equalTo(TopicName("namespace::topics_three")))
    }
}
