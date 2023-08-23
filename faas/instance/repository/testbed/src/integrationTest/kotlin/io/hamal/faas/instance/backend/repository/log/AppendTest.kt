package io.hamal.faas.instance.backend.repository.log

import io.hamal.backend.repository.api.log.CreateTopic
import io.hamal.backend.repository.api.log.GroupId
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.faas.instance.backend.repository.AbstractTest
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.TestFactory
import java.math.BigInteger

class AppendTest : AbstractTest() {

    @TestFactory
    fun `Append value to topic with long cmd id`() = runWith(LogBrokerRepository::class) { testInstance ->
        val topic = testInstance.create(
            CmdId(1),
            CreateTopic.TopicToCreate(TopicId(1), TopicName("test-topic"))
        )
        testInstance.append(
            CmdId(BigInteger("380896718712995851145215087")),
            topic,
            "some-content-1".toByteArray()
        )
        testInstance.append(
            CmdId(BigInteger("380896718712995851145215088")),
            topic,
            "some-content-2".toByteArray()
        )

        val result = testInstance.consume(
            GroupId("group-id"),
            testInstance.findTopic(TopicName("test-topic"))!!,
            1_000
        )
        assertThat(result, Matchers.hasSize(2))
    }
}