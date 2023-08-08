package io.hamal.backend.instance.req.handler.topic

import io.hamal.backend.instance.req.handler.BaseReqHandlerTest
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.req.ReqStatus
import io.hamal.lib.domain.req.SubmittedCreateTopicReq
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

internal class CreateTopicHandlerTest : BaseReqHandlerTest() {
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
                SubmittedCreateTopicReq(
                    reqId = ReqId(2),
                    status = ReqStatus.Submitted,
                    id = TopicId(2345),
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
                SubmittedCreateTopicReq(
                    reqId = ReqId(2),
                    status = ReqStatus.Submitted,
                    id = TopicId(3456),
                    name = TopicName("some-topic-name")
                )
            )
        }
        assertThat(exception.message, equalTo("Topic already exists"))

        verifySingleTopicExists()
    }

    private fun verifySingleTopicExists() {
        eventBrokerRepository.listTopics().also { topics ->
            assertThat(topics, hasSize(1))
            with(topics.first()) {
                assertThat(id, equalTo(TopicId(2345)))
                assertThat(name, equalTo(TopicName("some-topic-name")))
            }
        }
    }

    @Autowired
    private lateinit var testInstance: CreateTopicHandler

    private val submittedCreateTopicReq = SubmittedCreateTopicReq(
        reqId = ReqId(1),
        status = ReqStatus.Submitted,
        id = TopicId(2345),
        name = TopicName("some-topic-name")
    )
}