package io.hamal.backend.instance.req.handler.topic

import io.hamal.backend.instance.req.handler.BaseReqHandlerTest
import io.hamal.repository.api.log.LogChunkId
import io.hamal.repository.api.log.LogSegment
import io.hamal.repository.api.submitted_req.SubmittedAppendToTopicReq
import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus.Submitted
import io.hamal.lib.domain.vo.EventPayload
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

internal class AppendToTopicHandlerTest : BaseReqHandlerTest() {
    @Test
    fun `Appends event to topic`() {
        val topic = createTopic(TopicId(4444), TopicName("topic"))

        testInstance(
            SubmittedAppendToTopicReq(
                reqId = ReqId(SnowflakeId(123)),
                status = Submitted,
                id = TopicId(4444),
                payload = EventPayload(MapType(mutableMapOf("hamal" to StringType("rockz"))))
            )
        )

        eventBrokerRepository.read(LogChunkId(0), topic, 1).also { payloads ->
            assertThat(payloads, hasSize(1))

            @OptIn(ExperimentalSerializationApi::class) with(payloads.first()) {
                assertThat(segmentId, equalTo(LogSegment.Id(0)))
                assertThat(id, equalTo(LogChunkId(1)))
                assertThat(topicId, equalTo(TopicId(4444)))

                val payload = ProtoBuf { }.decodeFromByteArray(EventPayload.serializer(), bytes)
                assertThat(payload.value, equalTo(MapType(mutableMapOf("hamal" to StringType("rockz")))))
            }
        }
    }

    @Test
    fun `Tries to append event to topic which does not exists`() {
        val exception = assertThrows<NoSuchElementException> {
            testInstance(
                SubmittedAppendToTopicReq(
                    reqId = ReqId(SnowflakeId(123)),
                    status = Submitted,
                    id = TopicId(123),
                    payload = EventPayload(MapType(mutableMapOf("hamal" to StringType("rockz"))))
                )
            )
        }
        assertThat(exception.message, equalTo("Topic not found"))
    }

    @Autowired
    private lateinit var testInstance: AppendToTopicHandler
}