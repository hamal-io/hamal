package io.hamal.repository.memory.log

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.repository.api.log.ConsumerId
import io.hamal.repository.api.log.CreateTopic.TopicToCreate
import io.hamal.repository.api.log.ProtobufAppender
import io.hamal.repository.api.log.ProtobufLogConsumer
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class ConsumerTest {
    @Test
    fun `Best effort to consume chunk once`() {
        MemoryBrokerRepository().use { brokerRepository ->
            val topic = brokerRepository.create(
                CmdId(123),
                TopicToCreate(TopicId(123), TopicName("topic"), FlowId(23), GroupId(1))
            )

            val appender = ProtobufAppender(String::class, brokerRepository)
            IntRange(1, 10).forEach { appender.append(CmdId(it), topic, "$it") }
        }

        MemoryBrokerRepository().use { brokerRepository ->
            val topic = brokerRepository.create(
                CmdId(123),
                TopicToCreate(TopicId(123), TopicName("topic"), FlowId(23), GroupId(1))
            )

            val testInstance = ProtobufLogConsumer(ConsumerId("consumer-01"), topic, brokerRepository, String::class)
            testInstance.consumeIndexed(10) { index, _, value ->
                assertThat("${index + 1}", equalTo(value))
            }
        }
    }
}