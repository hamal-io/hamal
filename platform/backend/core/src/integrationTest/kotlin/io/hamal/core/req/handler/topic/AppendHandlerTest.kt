package io.hamal.core.request.handler.topic

import io.hamal.core.request.handler.BaseReqHandlerTest
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.Serde
import io.hamal.lib.domain._enum.RequestStatus.Submitted
import io.hamal.lib.domain.request.TopicAppendToRequested
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.domain.vo.TopicEntryPayload
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.repository.api.log.ChunkId
import io.hamal.repository.api.log.Segment
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
            TopicAppendToRequested(
                id = RequestId(SnowflakeId(123)),
                status = Submitted,
                topicId = TopicId(4444),
                groupId = testGroup.id,
                payload = TopicEntryPayload(MapType(mutableMapOf("hamal" to StringType("rockz"))))
            )
        )

        eventBrokerRepository.read(ChunkId(0), topic, 1).also { payloads ->
            assertThat(payloads, hasSize(1))

            with(payloads.first()) {
                assertThat(segmentId, equalTo(Segment.Id(0)))
                assertThat(id, equalTo(ChunkId(1)))
                assertThat(topicId, equalTo(TopicId(4444)))
                
                val payload = Serde.decompressAndDeserialize(TopicEntryPayload::class, bytes)
                assertThat(payload.value, equalTo(MapType(mutableMapOf("hamal" to StringType("rockz")))))
            }
        }
    }

    @Test
    fun `Tries to append entry to topic which does not exists`() {
        val exception = assertThrows<NoSuchElementException> {
            testInstance(
                TopicAppendToRequested(
                    id = RequestId(SnowflakeId(123)),
                    status = Submitted,
                    topicId = TopicId(123),
                    groupId = testGroup.id,
                    payload = TopicEntryPayload(MapType(mutableMapOf("hamal" to StringType("rockz"))))
                )
            )
        }
        assertThat(exception.message, equalTo("Topic not found"))
    }

    @Autowired
    private lateinit var testInstance: AppendToHandler
}