package io.hamal.repository.memory.log

import io.hamal.lib.domain.vo.LogTopicId.Companion.LogTopicId
import io.hamal.repository.api.log.LogConsumerId.Companion.LogConsumerId
import io.hamal.repository.api.log.LogEventId.Companion.LogEventId
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class LogBrokerMemoryRepositoryTest {

    @Nested
    inner class NextEntryIdTest {
        @Test
        fun `Returns entry id 0 if no entry exists for consumer id and topic id`() {
            val result = testInstance.nextEventId(LogConsumerId(1), LogTopicId(42))
            assertThat(result, equalTo(LogEventId(0)))
        }

        @Test
        fun `Next entry id - is last committed entry id plus 1`() {
            testInstance.commit(LogConsumerId(1), LogTopicId(1), LogEventId(127))

            val result = testInstance.nextEventId(LogConsumerId(1), LogTopicId(1))
            assertThat(result, equalTo(LogEventId(128)))
        }

        @Test
        fun `Does not return next entry id of different topic`() {
            testInstance.commit(LogConsumerId(1), LogTopicId(1), LogEventId(127))

            val result = testInstance.nextEventId(LogConsumerId(1), LogTopicId(2))
            assertThat(result, equalTo(LogEventId(0)))
        }

        @Test
        fun `Does not return next chunk id of different consumer`() {
            testInstance.commit(LogConsumerId(42), LogTopicId(1), LogEventId(127))

            val result = testInstance.nextEventId(LogConsumerId(1337), LogTopicId(1))
            assertThat(result, equalTo(LogEventId(0)))
        }

        private val testInstance = LogBrokerMemoryRepository()
    }

}