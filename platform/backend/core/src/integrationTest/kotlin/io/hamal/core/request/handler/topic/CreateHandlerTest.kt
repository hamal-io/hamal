package io.hamal.core.request.handler.topic

import io.hamal.core.request.handler.BaseReqHandlerTest
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain._enum.RequestStatus.Submitted
import io.hamal.lib.domain._enum.TopicType
import io.hamal.lib.domain.request.TopicGroupCreateRequested
import io.hamal.lib.domain.vo.LogTopicId
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.repository.api.TopicQueryRepository.TopicQuery
import io.hamal.repository.api.log.LogBrokerRepository
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired


internal class TopicGroupCreateHandlerTest : BaseReqHandlerTest() {
    @Test
    fun `Creates topic with id`() {
        logBrokerRepository.clear()

        testInstance(submittedCreateTopicReq)

        verifySingleTopicExists()
    }

    private fun verifySingleTopicExists() {
        topicQueryRepository.list(TopicQuery(types = listOf(TopicType.Group), limit = Limit(100))).also { topics ->
            assertThat(topics, hasSize(1))
            with(topics.first()) {
                assertThat(id, equalTo(TopicId(2345)))
                assertThat(name, equalTo(TopicName("some-topic-name")))
                assertThat(logTopicId, equalTo(LogTopicId(3456)))
                assertThat(groupId, equalTo(testGroup.id))
                assertThat(type, equalTo(TopicType.Group))
            }
        }

        logBrokerRepository.listTopics(LogBrokerRepository.LogTopicQuery(limit = Limit(100))).also { logTopics ->
            assertThat(logTopics, hasSize(1))
            with(logTopics.first()) {
                assertThat(id, equalTo(LogTopicId(3456)))
            }
        }
    }

    @Autowired
    private lateinit var testInstance: TopicGroupCreateHandler

    private val submittedCreateTopicReq by lazy {
        TopicGroupCreateRequested(
            id = RequestId(1),
            status = Submitted,
            topicId = TopicId(2345),
            logTopicId = LogTopicId(3456),
            groupId = testGroup.id,
            name = TopicName("some-topic-name")
        )
    }
}