package io.hamal.backend.instance.req.handler.event

import io.hamal.backend.instance.req.handler.BaseReqHandlerIT
import io.hamal.backend.repository.api.log.LogChunkId
import io.hamal.backend.repository.api.log.LogSegment
import io.hamal.backend.repository.memory.log.MemoryLogTopic
import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.Event
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.req.ReqStatus
import io.hamal.lib.domain.req.SubmittedAppendEventReq
import io.hamal.lib.domain.vo.Content
import io.hamal.lib.domain.vo.ContentType
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

internal class AppendEventHandlerIT : BaseReqHandlerIT() {
    @Test
    fun `Appends event to topic`() {
        val topic = createTopic(TopicId(4444), TopicName("topic"))

        testInstance(
            SubmittedAppendEventReq(
                id = ReqId(SnowflakeId(123)),
                status = ReqStatus.Submitted,
                topicId = TopicId(4444),
                event = Event(
                    contentType = ContentType("application/json"),
                    content = Content("""{"hamal":"rockz"}""")
                )
            )
        )

        eventBrokerRepository.read(LogChunkId(0), topic, 1).also { evts ->
            assertThat(evts, hasSize(1))

            @OptIn(ExperimentalSerializationApi::class)
            with(evts.first()) {
                assertThat(segmentId, equalTo(LogSegment.Id(0)))
                assertThat(id, equalTo(LogChunkId(1)))
                assertThat(topicId, equalTo(TopicId(4444)))

                val evt = ProtoBuf { }.decodeFromByteArray(Event.serializer(), bytes)
                assertThat(evt.contentType, equalTo(ContentType("application/json")))
                assertThat(evt.content, equalTo(Content("""{"hamal":"rockz"}""")))
            }
        }
    }

    @Test
    fun `Tries to append event to topic which does not exists`() {
        val exception = assertThrows<NoSuchElementException> {
            testInstance(
                SubmittedAppendEventReq(
                    id = ReqId(SnowflakeId(123)),
                    status = ReqStatus.Submitted,
                    topicId = TopicId(123),
                    event = Event(
                        contentType = ContentType("application/json"),
                        content = Content("""{"hamal":"rockz"}""")
                    )
                )
            )
        }
        assertThat(exception.message, equalTo("Topic not found"))
    }

    @Autowired
    private lateinit var testInstance: AppendEventHandler<MemoryLogTopic>
}