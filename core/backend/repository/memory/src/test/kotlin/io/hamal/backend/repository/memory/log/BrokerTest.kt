package io.hamal.backend.repository.memory.log

import io.hamal.backend.repository.api.log.CreateTopic.TopicToCreate
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test


class MemoryLogBrokerRepositoryTest {
    @Nested
    inner class CreateTopicTest {
        @Test
        fun `Bug - Able to resolve real topic`() {
            val testInstance = MemoryLogBrokerRepository()

            val result = testInstance.create(
                CmdId(123),
                TopicToCreate(TopicId(234), TopicName("scheduler::flow_enqueued"))
            )

            assertThat(result.id, equalTo(TopicId(234)))
            assertThat(result.name, equalTo(TopicName("scheduler::flow_enqueued")))
        }
    }
}
