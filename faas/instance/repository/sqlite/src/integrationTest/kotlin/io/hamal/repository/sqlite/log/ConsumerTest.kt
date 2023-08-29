package io.hamal.repository.sqlite.log

import io.hamal.backend.repository.api.log.CreateTopic
import io.hamal.backend.repository.api.log.GroupId
import io.hamal.backend.repository.api.log.ProtobufAppender
import io.hamal.backend.repository.api.log.ProtobufLogConsumer
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import java.nio.file.Files

class ConsumerTest {
    @Test
    fun `Best effort to consume chunk once`() {
        val path = Files.createTempDirectory("broker_it")

        io.hamal.repository.sqlite.log.SqliteLogBrokerRepository(io.hamal.repository.sqlite.log.SqliteLogBroker(path))
            .use { brokerRepository ->
            val topic = brokerRepository.create(
                CmdId(1),
                CreateTopic.TopicToCreate(TopicId(123), TopicName("topic"))
            )
            val appender = ProtobufAppender(String::class, brokerRepository)
            IntRange(1, 10).forEach { appender.append(CmdId(it), topic, "$it") }
        }

        io.hamal.repository.sqlite.log.SqliteLogBrokerRepository(io.hamal.repository.sqlite.log.SqliteLogBroker(path))
            .use { brokerRepository ->
            val topic = brokerRepository.findTopic(TopicName("topic"))!!
            val testInstance = ProtobufLogConsumer(GroupId("consumer-01"), topic, brokerRepository, String::class)
            testInstance.consumeIndexed(10) { index, _, value ->
                assertThat("${index + 1}", equalTo(value))
            }
        }
    }
}