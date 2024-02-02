package io.hamal.repository.memory.new_log

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.repository.api.new_log.*
import io.hamal.repository.api.new_log.LogBrokerRepository.LogTopicToCreate
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class ConsumerTest {
    @Test
    fun `Best effort to consume chunk once`() {
        LogBrokerMemoryRepository().use { brokerRepository ->
            val topic = brokerRepository.create(
                CmdId(123),
                LogTopicToCreate(LogTopicId(123), LogTopicName("topic"), LogTopicGroupId(1))
            )

            val appender = LogTopicAppenderImpl<String>(brokerRepository)
            IntRange(1, 10).forEach { appender.append(CmdId(it), topic.id, "$it") }
        }

        LogBrokerMemoryRepository().use { brokerRepository ->
            val topic = brokerRepository.create(
                CmdId(123),
                LogTopicToCreate(LogTopicId(123), LogTopicName("topic"), LogTopicGroupId(1))
            )

            val testInstance = LogConsumerImpl(LogConsumerId(1), topic.id, brokerRepository, String::class)
            testInstance.consumeIndexed(Limit(10)) { index, _, value ->
                assertThat("${index + 1}", equalTo(value))
            }
        }
    }
}