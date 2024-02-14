package io.hamal.core.request.handler.topic

import io.hamal.core.request.handler.BaseReqHandlerTest
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain._enum.RequestStatus.Submitted
import io.hamal.lib.domain._enum.TopicType.Namespace
import io.hamal.lib.domain.request.TopicCreateRequested
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


internal class TopicCreateHandlerTest : BaseReqHandlerTest() {
    @Test
    fun `Creates topic`() {
        logBrokerRepository.clear()

        testInstance(topicCreateRequested)

        verifySingleTopicExists()
    }

    private fun verifySingleTopicExists() {
        topicQueryRepository.list(TopicQuery(types = listOf(Namespace), limit = Limit(100))).also { topics ->
            assertThat(topics, hasSize(1))
            with(topics.first()) {
                assertThat(id, equalTo(TopicId(2345)))
                assertThat(name, equalTo(TopicName("some-topic-name")))
                assertThat(logTopicId, equalTo(LogTopicId(3456)))
                assertThat(workspaceId, equalTo(testWorkspace.id))
                assertThat(namespaceId, equalTo(testNamespace.id))
                assertThat(type, equalTo(Namespace))
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
    private lateinit var testInstance: TopicCreateHandler

    private val topicCreateRequested by lazy {
        TopicCreateRequested(
            id = RequestId(1),
            status = Submitted,
            topicId = TopicId(2345),
            logTopicId = LogTopicId(3456),
            workspaceId = testWorkspace.id,
            namespaceId = testNamespace.id,
            type = Namespace,
            name = TopicName("some-topic-name")
        )
    }
}