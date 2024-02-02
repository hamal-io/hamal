package io.hamal.repository.memory.new_log

import io.hamal.repository.api.new_log.LogConsumerId
import io.hamal.repository.api.new_log.LogEntryId
import io.hamal.repository.api.new_log.LogTopicId
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class LogBrokerMemoryRepositoryTest {

    @Nested
    inner class NextEntryIdTest {
        @Test
        fun `Returns entry id 0 if no entry exists for consumer id and topic id`() {
            val result = testInstance.nextEntryId(LogConsumerId(1), LogTopicId(42))
            assertThat(result, equalTo(LogEntryId(0)))
        }

        @Test
        fun `Next entry id - is last committed entry id plus 1`() {
            testInstance.commit(LogConsumerId(1), LogTopicId(1), LogEntryId(127))

            val result = testInstance.nextEntryId(LogConsumerId(1), LogTopicId(1))
            assertThat(result, equalTo(LogEntryId(128)))
        }

        @Test
        fun `Does not return next entry id of different topic`() {
            testInstance.commit(LogConsumerId(1), LogTopicId(1), LogEntryId(127))

            val result = testInstance.nextEntryId(LogConsumerId(1), LogTopicId(2))
            assertThat(result, equalTo(LogEntryId(0)))
        }

        @Test
        fun `Does not return next chunk id of different consumer`() {
            testInstance.commit(LogConsumerId(42), LogTopicId(1), LogEntryId(127))

            val result = testInstance.nextEntryId(LogConsumerId(1337), LogTopicId(1))
            assertThat(result, equalTo(LogEntryId(0)))
        }

        private val testInstance = LogBrokerMemoryRepository()
    }

}