package io.hamal.backend.instance.web.event


import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test

internal class CreateTopicRouteIT : BaseEventRouteIT() {
    @Test
    fun `Create topic`() {
        val result = createTopic(TopicName("namespace::topics_one"))
        Thread.sleep(100)

        verifyReqCompleted(result.id)
        verifyTopicCreated(result.topicId)

        with(listTopics()) {
            assertThat(topics, hasSize(1))
            val topic = topics.first()
            assertThat(topic.name, equalTo(TopicName("namespace::topics_one")))
        }
    }

    @Test
    fun `Create topic name already exists`() {
        createTopic(TopicName("namespace::topics_one"))

        with(createTopic(TopicName("namespace::topics_one"))) {
            Thread.sleep(100)
            verifyReqFailed(id)
        }

        with(listTopics()) {
            assertThat(topics, hasSize(1))
            val topic = topics.first()
            assertThat(topic.name, equalTo(TopicName("namespace::topics_one")))
        }
    }
}

private fun CreateTopicRouteIT.verifyTopicCreated(topicId: TopicId) {
    with(eventQueryService.findTopic(topicId)!!) {
        assertThat(id, equalTo(topicId))
        assertThat(name, equalTo(TopicName("namespace::topics_one")))
    }
}
