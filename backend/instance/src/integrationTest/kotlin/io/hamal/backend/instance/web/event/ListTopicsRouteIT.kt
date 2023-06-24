package io.hamal.backend.instance.web.event


import io.hamal.backend.instance.service.query.EventQueryService
import io.hamal.backend.repository.api.ReqQueryRepository
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.lib.domain.vo.TopicName
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.server.LocalServerPort


internal class ListTopicsRouteIT(
    @LocalServerPort localPort: Int,
    @Autowired reqQueryRepository: ReqQueryRepository,
    @Autowired eventQueryService: EventQueryService<*>,
    @Autowired eventBrokerRepository: LogBrokerRepository<*>
) : BaseEventRouteIT(
    localPort = localPort,
    reqQueryRepository = reqQueryRepository,
    eventQueryService = eventQueryService,
    eventBrokerRepository = eventBrokerRepository
) {
    @Test
    fun `No topics`() {
        val result = listTopics()
        assertThat(result.topics, empty())
    }

    @Test
    fun `Single topic`() {
        createTopic(TopicName("namespace::topics_one"))
        Thread.sleep(100)

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
        Thread.sleep(100)

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
