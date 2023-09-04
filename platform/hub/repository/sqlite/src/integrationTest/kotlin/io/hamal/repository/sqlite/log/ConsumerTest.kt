package io.hamal.repository.sqlite.log

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.repository.api.log.CreateTopic
import io.hamal.repository.api.log.GroupId
import io.hamal.repository.api.log.ProtobufAppender
import io.hamal.repository.api.log.ProtobufLogConsumer
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import java.nio.file.Files

class ConsumerTest {
    @Test
    fun `Best effort to consume chunk once`() {
        val path = Files.createTempDirectory("broker_it")

        SqliteBrokerRepository(SqliteBroker(path))
            .use { brokerRepository ->
                val topic = brokerRepository.create(
                    CmdId(1),
                    CreateTopic.TopicToCreate(TopicId(123), TopicName("topic"))
                )
                val appender = ProtobufAppender(String::class, brokerRepository)
                IntRange(1, 10).forEach { appender.append(CmdId(it), topic, "$it") }
            }

        SqliteBrokerRepository(SqliteBroker(path))
            .use { brokerRepository ->
                val topic = brokerRepository.findTopic(TopicName("topic"))!!
                val testInstance = ProtobufLogConsumer(GroupId("consumer-01"), topic, brokerRepository, String::class)
                testInstance.consumeIndexed(10) { index, _, value ->
                    assertThat("${index + 1}", equalTo(value))
                }
            }
    }
}