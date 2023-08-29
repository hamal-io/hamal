package io.hamal.repository.memory.log

import io.hamal.backend.repository.api.log.GroupId
import io.hamal.backend.repository.api.log.LogChunkId
import io.hamal.lib.domain.vo.TopicId
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test


class MemoryLogBrokerConsumersRepositoryTest {
    @Nested
    inner class NextChunkTopicIdTest {

        @BeforeEach
        fun setup() {
            testInstance.clear()
        }

        @AfterEach
        fun after() {
            testInstance.close()
        }

        @Test
        fun `Returns chunk id 0 if no entry exists for group id and topic id`() {
            val result = testInstance.nextChunkId(GroupId("some-group-id"), TopicId(42))
            assertThat(result, equalTo(LogChunkId(0)))
            assertThat(testInstance.count(), equalTo(0UL))
        }

        @Test
        fun `Next chunk id - is last committed chunk id plus 1`() {
            testInstance.commit(GroupId("some-group-id"), TopicId(1), LogChunkId(127))
            val result = testInstance.nextChunkId(GroupId("some-group-id"), TopicId(1))
            assertThat(result, equalTo(LogChunkId(128)))
            assertThat(testInstance.count(), equalTo(1UL))
        }

        @Test
        fun `Does not return next chunk id of different topic`() {
            testInstance.commit(GroupId("some-group-id"), TopicId(1), LogChunkId(127))

            val result = testInstance.nextChunkId(GroupId("some-group-id"), TopicId(2))
            assertThat(result, equalTo(LogChunkId(0)))
            assertThat(testInstance.count(), equalTo(1UL))
        }

        @Test
        fun `Does not return next chunk id of different group`() {
            testInstance.commit(GroupId("some-group-id"), TopicId(1), LogChunkId(127))
            val result = testInstance.nextChunkId(GroupId("different-group-id"), TopicId(1))
            assertThat(result, equalTo(LogChunkId(0)))
            assertThat(testInstance.count(), equalTo(1UL))
        }

        private val testInstance = MemoryLogBrokerConsumersRepository()
    }

    @Nested
    inner class CommitTest {

        @BeforeEach
        fun setup() {
            testInstance.clear()
        }

        @AfterEach
        fun after() {
            testInstance.close()
        }

        @Test
        fun `Never committed before`() {
            testInstance.commit(GroupId("some-group"), TopicId(123), LogChunkId(23))
            assertThat(testInstance.count(), equalTo(1UL))
        }

        @Test
        fun `Committed before`() {
            testInstance.commit(GroupId("some-group"), TopicId(123), LogChunkId(23))

            testInstance.commit(GroupId("some-group"), TopicId(123), LogChunkId(1337))
            assertThat(testInstance.count(), equalTo(1UL))
        }

        @Test
        fun `Does not overwrite different topic id `() {
            testInstance.commit(GroupId("some-group"), TopicId(23), LogChunkId(1))
            testInstance.commit(GroupId("some-group"), TopicId(34), LogChunkId(2))

            assertThat(testInstance.count(), equalTo(2UL))
        }

        @Test
        fun `Does not overwrite different group id`() {
            testInstance.commit(GroupId("some-group"), TopicId(23), LogChunkId(1))
            testInstance.commit(GroupId("another-group"), TopicId(23), LogChunkId(2))

            assertThat(testInstance.count(), equalTo(2UL))
        }

        private val testInstance = MemoryLogBrokerConsumersRepository()
    }
}