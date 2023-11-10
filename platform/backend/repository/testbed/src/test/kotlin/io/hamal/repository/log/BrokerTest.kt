package io.hamal.repository.log

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.repository.api.log.BrokerRepository
import io.hamal.repository.api.log.CreateTopic.TopicToCreate
import io.hamal.repository.fixture.AbstractUnitTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory


class BrokerRepositoryTest : AbstractUnitTest() {
    @Nested
    inner class CreateTopicTest {

        @TestFactory
        fun `Bug - Able to resolve real topic`() = runWith(BrokerRepository::class) {
            val result = create(
                CmdId(123),
                TopicToCreate(TopicId(234), TopicName("scheduler::flow_enqueued"), FlowId(23), GroupId(1))
            )

            assertThat(result.id, equalTo(TopicId(234)))
            assertThat(result.name, equalTo(TopicName("scheduler::flow_enqueued")))
        }
    }
}
