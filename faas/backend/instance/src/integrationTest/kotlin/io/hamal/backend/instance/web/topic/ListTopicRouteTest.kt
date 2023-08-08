package io.hamal.backend.instance.web.topic


import io.hamal.lib.domain.vo.TopicName
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test


internal class ListTopicRouteTest : BaseTopicRouteTest() {
    @Test
    fun `No topics`() {
        val result = listTopics()
        assertThat(result.topics, empty())
    }

    @Test
    fun `Single topic`() {
        awaitCompleted(createTopic(TopicName("namespace::topics_one")))

        with(listTopics()) {
            assertThat(topics, hasSize(1))

            with(topics.first()) {
                assertThat(name, equalTo(TopicName("namespace::topics_one")))
            }
        }
    }

    @Test
    fun `Multiple topics`() {
        awaitCompleted(
            createTopic(TopicName("namespace::topics_one")),
            createTopic(TopicName("namespace::topics_two")),
            createTopic(TopicName("namespace::topics_three"))
        )

        with(listTopics()) {
            assertThat(topics, hasSize(3))

            val topicOne = topics[0]
            assertThat(topicOne.name, equalTo(TopicName("namespace::topics_one")))

            val topicTwo = topics[1]
            assertThat(topicTwo.name, equalTo(TopicName("namespace::topics_two")))

            val topicThree = topics[2]
            assertThat(topicThree.name, equalTo(TopicName("namespace::topics_three")))
        }
    }
}
