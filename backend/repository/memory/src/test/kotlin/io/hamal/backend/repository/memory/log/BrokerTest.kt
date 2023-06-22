package io.hamal.backend.repository.memory.log

import io.hamal.backend.repository.api.log.LogBroker
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test


class MemoryLogBrokerRepositoryTest {
    @Nested
    inner class ResolveTopicTest {
        @Test
        fun `Bug - Able to resolve real topic`() {
            val testInstance = MemoryLogBrokerRepository(MemoryLogBroker(LogBroker.Id(456)))

            val result = testInstance.resolveTopic(TopicName("scheduler::flow_enqueued"))
            assertThat(result.id, equalTo(TopicId(1)))
            assertThat(result.logBrokerId, equalTo(LogBroker.Id(456)))
            assertThat(result.name, equalTo(TopicName("scheduler::flow_enqueued")))
        }
    }
}
