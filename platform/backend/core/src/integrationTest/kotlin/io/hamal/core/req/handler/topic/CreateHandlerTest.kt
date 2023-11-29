package io.hamal.core.req.handler.topic

import io.hamal.core.req.handler.BaseReqHandlerTest
import io.hamal.lib.domain._enum.ReqStatus.Submitted
import io.hamal.lib.domain.vo.ReqId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.repository.api.log.BrokerTopicsRepository.TopicQuery
import io.hamal.repository.api.submitted_req.TopicCreateSubmitted
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired


internal object TopicCreateHandlerTest : BaseReqHandlerTest() {
    @Test
    fun `Creates topic with id and name`() {
        testInstance(submittedCreateTopicReq)

        verifySingleTopicExists()
    }

    @Test
    fun `Topic with id already exists`() {
        testInstance(submittedCreateTopicReq)

        val exception = assertThrows<IllegalArgumentException> {
            testInstance(
                TopicCreateSubmitted(
                    id = ReqId(2),
                    status = Submitted,
                    topicId = TopicId(2345),
                    flowId = testFlow.id,
                    groupId = testGroup.id,
                    name = TopicName("another-topic-name")
                )
            )
        }
        assertThat(exception.message, equalTo("Topic already exists"))

        verifySingleTopicExists()
    }

    @Test
    fun `Topic with name already exists`() {
        testInstance(submittedCreateTopicReq)

        val exception = assertThrows<IllegalArgumentException> {
            testInstance(
                TopicCreateSubmitted(
                    id = ReqId(2),
                    status = Submitted,
                    topicId = TopicId(3456),
                    flowId = testFlow.id,
                    groupId = testGroup.id,
                    name = TopicName("some-topic-name")
                )
            )
        }
        assertThat(exception.message, equalTo("Topic already exists"))

        verifySingleTopicExists()
    }

    private fun verifySingleTopicExists() {
        eventBrokerRepository.listTopics(TopicQuery(groupIds = listOf())).also { topics ->
            assertThat(topics, hasSize(1))
            with(topics.first()) {
                assertThat(id, equalTo(TopicId(2345)))
                assertThat(name, equalTo(TopicName("some-topic-name")))
            }
        }
    }

    @Autowired
    private lateinit var testInstance: TopicCreateHandler

    private val submittedCreateTopicReq by lazy {
        TopicCreateSubmitted(
            id = ReqId(1),
            status = Submitted,
            topicId = TopicId(2345),
            flowId = testFlow.id,
            groupId = testGroup.id,
            name = TopicName("some-topic-name")
        )
    }
}