package io.hamal.backend.repository.sqlite.log

import io.hamal.backend.repository.api.log.CreateTopic.TopicToCreate
import io.hamal.backend.repository.api.log.GroupId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import java.math.BigInteger
import java.nio.file.Files

class AppendIT {

    @Test
    fun `Append value to topic with long cmd id`() {
        val path = Files.createTempDirectory("append_it")

        SqliteLogBrokerRepository(SqliteLogBroker(path)).use { testInstance ->
            val topic = testInstance.create(CmdId(1), TopicToCreate(TopicId(1), TopicName("test-topic")))
            testInstance.append(CmdId(BigInteger("380896718712995851145215087")), topic, "some-content-1".toByteArray())
            testInstance.append(CmdId(BigInteger("380896718712995851145215088")), topic, "some-content-2".toByteArray())

            val result = testInstance.consume(
                GroupId("group-id"),
                testInstance.findTopic(TopicName("test-topic"))!!,
                1_000
            )
            assertThat(result, hasSize(2))
        }
    }
}