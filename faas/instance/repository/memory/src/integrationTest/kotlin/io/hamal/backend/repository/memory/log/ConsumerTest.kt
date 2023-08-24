package io.hamal.backend.repository.memory.log

import io.hamal.backend.repository.api.log.CreateTopic.TopicToCreate
import io.hamal.backend.repository.api.log.GroupId
import io.hamal.backend.repository.api.log.ProtobufAppender
import io.hamal.backend.repository.api.log.ProtobufLogConsumer
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class ConsumerTest {
    @Test
    fun `Best effort to consume chunk once`() {
        MemoryLogBrokerRepository().use { brokerRepository ->
            val topic = brokerRepository.create(
                CmdId(123),
                TopicToCreate(TopicId(123), TopicName("topic"))
            )

            val appender = ProtobufAppender(String::class, brokerRepository)
            IntRange(1, 10).forEach { appender.append(CmdId(it), topic, "$it") }
        }

        MemoryLogBrokerRepository().use { brokerRepository ->
            val topic = brokerRepository.create(
                CmdId(123),
                TopicToCreate(TopicId(123), TopicName("topic"))
            )

            val testInstance = ProtobufLogConsumer(GroupId("consumer-01"), topic, brokerRepository, String::class)
            testInstance.consumeIndexed(10) { index, _, value ->
                assertThat("${index + 1}", equalTo(value))
            }
        }
    }
}