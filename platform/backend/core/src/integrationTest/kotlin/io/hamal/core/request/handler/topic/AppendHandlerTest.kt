package io.hamal.core.request.handler.topic

import io.hamal.core.request.handler.BaseRequestHandlerTest
import io.hamal.lib.common.domain.Limit.Companion.Limit
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain._enum.RequestStatus.Submitted
import io.hamal.lib.domain.request.TopicAppendEventRequested
import io.hamal.lib.domain.vo.AuthId.Companion.AuthId
import io.hamal.lib.domain.vo.RequestId.Companion.RequestId
import io.hamal.lib.domain.vo.TopicEventPayload
import io.hamal.lib.domain.vo.TopicId.Companion.TopicId
import io.hamal.lib.domain.vo.TopicName.Companion.TopicName
import io.hamal.repository.api.log.LogEventId.Companion.LogEventId
import io.hamal.repository.record.json
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

internal class TopicAppendHandlerTest : BaseRequestHandlerTest() {

    @Test
    fun `Appends event to topic`() {
        val topic = createTopic(TopicId(4444), TopicName("topic"))

        testInstance(
            TopicAppendEventRequested(
                requestId = RequestId(SnowflakeId(123)),
                requestedBy = AuthId(2),
                requestStatus = Submitted,
                id = TopicId(4444),
                payload = TopicEventPayload(HotObject.builder().set("hamal", "rocks").build())
            )
        )

        logBrokerRepository.read(LogEventId(0), topic.logTopicId, Limit(100)).also { payloads ->
            assertThat(payloads, hasSize(1))

            with(payloads.first()) {
                assertThat(id, equalTo(LogEventId(1)))

                val payload = json.decompressAndDeserialize(TopicEventPayload::class, bytes)
                assertThat(payload.value, equalTo(HotObject.builder().set("hamal", "rocks").build()))
            }
        }
    }

    @Test
    fun `Tries to append event to topic which does not exists`() {
        val exception = assertThrows<NoSuchElementException> {
            testInstance(
                TopicAppendEventRequested(
                    requestId = RequestId(SnowflakeId(123)),
                    requestedBy = AuthId(2),
                    requestStatus = Submitted,
                    id = TopicId(123),
                    payload = TopicEventPayload(HotObject.builder().set("hamal", "rocks").build())
                )
            )
        }
        assertThat(exception.message, equalTo("Topic not found"))
    }

    @Autowired
    private lateinit var testInstance: AppendToHandler
}