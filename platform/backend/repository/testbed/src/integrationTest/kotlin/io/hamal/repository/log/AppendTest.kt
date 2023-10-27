package io.hamal.repository.log

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.repository.api.log.BrokerRepository
import io.hamal.repository.api.log.ConsumerId
import io.hamal.repository.api.log.CreateTopic.TopicToCreate
import io.hamal.repository.fixture.AbstractIntegrationTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.TestFactory
import java.math.BigInteger

class AppendTest : AbstractIntegrationTest() {

    @TestFactory
    fun `Append value to topic with long cmd id`() = runWith(BrokerRepository::class) { testInstance ->
        val topic = testInstance.create(
            CmdId(1),
            TopicToCreate(TopicId(1), TopicName("test-topic"), NamespaceId(1), GroupId(1))
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
            ConsumerId("group-id"),
            testInstance.findTopic(NamespaceId(1), TopicName("test-topic"))!!,
            1_000
        )
        assertThat(result, Matchers.hasSize(2))
    }
}