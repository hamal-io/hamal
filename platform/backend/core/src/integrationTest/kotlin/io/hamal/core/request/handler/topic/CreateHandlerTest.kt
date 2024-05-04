package io.hamal.core.request.handler.topic

import io.hamal.core.request.handler.BaseRequestHandlerTest
import io.hamal.lib.common.domain.Limit.Companion.Limit
import io.hamal.lib.domain._enum.RequestStatus.Submitted
import io.hamal.lib.domain._enum.TopicTypes.Namespace
import io.hamal.lib.domain.request.TopicCreateRequested
import io.hamal.lib.domain.vo.AuthId.Companion.AuthId
import io.hamal.lib.domain.vo.LogTopicId.Companion.LogTopicId
import io.hamal.lib.domain.vo.RequestId.Companion.RequestId
import io.hamal.lib.domain.vo.TopicId.Companion.TopicId
import io.hamal.lib.domain.vo.TopicName.Companion.TopicName
import io.hamal.lib.domain.vo.TopicType.Companion.TopicType
import io.hamal.repository.api.TopicQueryRepository.TopicQuery
import io.hamal.repository.api.log.LogBrokerRepository
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired


internal class TopicCreateHandlerTest : BaseRequestHandlerTest() {
    @Test
    fun `Creates topic`() {
        logBrokerRepository.clear()

        testInstance(topicCreateRequested)

        verifySingleTopicExists()
    }

    private fun verifySingleTopicExists() {
        topicQueryRepository.list(TopicQuery(types = listOf(TopicType(Namespace)), limit = Limit(100))).also { topics ->
            assertThat(topics, hasSize(1))
            with(topics.first()) {
                assertThat(id, equalTo(TopicId(2345)))
                assertThat(name, equalTo(TopicName("some-topic-name")))
                assertThat(logTopicId, equalTo(LogTopicId(3456)))
                assertThat(workspaceId, equalTo(testWorkspace.id))
                assertThat(namespaceId, equalTo(testNamespace.id))
                assertThat(type, equalTo(TopicType(Namespace)))
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
            requestId = RequestId(1),
            requestedBy = AuthId(2),
            requestStatus = Submitted,
            id = TopicId(2345),
            logTopicId = LogTopicId(3456),
            workspaceId = testWorkspace.id,
            namespaceId = testNamespace.id,
            type = Namespace,
            name = TopicName("some-topic-name")
        )
    }
}