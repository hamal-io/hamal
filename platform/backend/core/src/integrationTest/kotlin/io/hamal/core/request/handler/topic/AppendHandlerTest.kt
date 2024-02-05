package io.hamal.core.request.handler.topic

import io.hamal.core.request.handler.BaseReqHandlerTest
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain._enum.RequestStatus.Submitted
import io.hamal.lib.domain.request.TopicAppendEventRequested
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.domain.vo.TopicEventPayload
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.repository.api.log.LogEventId
import io.hamal.repository.record.json
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

internal class TopicAppendHandlerTest : BaseReqHandlerTest() {

    @Test
    fun `Appends entry to topic`() {
        val topic = createTopic(TopicId(4444), TopicName("topic"))

        testInstance(
            TopicAppendEventRequested(
                id = RequestId(SnowflakeId(123)),
                status = Submitted,
                topicId = TopicId(4444),
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
    fun `Tries to append entry to topic which does not exists`() {
        val exception = assertThrows<NoSuchElementException> {
            testInstance(
                TopicAppendEventRequested(
                    id = RequestId(SnowflakeId(123)),
                    status = Submitted,
                    topicId = TopicId(123),
                    payload = TopicEventPayload(HotObject.builder().set("hamal", "rocks").build())
                )
            )
        }
        assertThat(exception.message, equalTo("Topic not found"))
    }

    @Autowired
    private lateinit var testInstance: AppendToHandler
}