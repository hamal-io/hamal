package io.hamal.repository.memory.log

import io.hamal.lib.domain.vo.TopicId
import io.hamal.repository.api.log.ChunkId
import io.hamal.repository.api.log.ConsumerId
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
            val result = testInstance.nextChunkId(ConsumerId("some-group-id"), TopicId(42))
            assertThat(result, equalTo(ChunkId(0)))
            assertThat(testInstance.count(), equalTo(0UL))
        }

        @Test
        fun `Next chunk id - is last committed chunk id plus 1`() {
            testInstance.commit(ConsumerId("some-group-id"), TopicId(1), ChunkId(127))
            val result = testInstance.nextChunkId(ConsumerId("some-group-id"), TopicId(1))
            assertThat(result, equalTo(ChunkId(128)))
            assertThat(testInstance.count(), equalTo(1UL))
        }

        @Test
        fun `Does not return next chunk id of different topic`() {
            testInstance.commit(ConsumerId("some-group-id"), TopicId(1), ChunkId(127))

            val result = testInstance.nextChunkId(ConsumerId("some-group-id"), TopicId(2))
            assertThat(result, equalTo(ChunkId(0)))
            assertThat(testInstance.count(), equalTo(1UL))
        }

        @Test
        fun `Does not return next chunk id of different group`() {
            testInstance.commit(ConsumerId("some-group-id"), TopicId(1), ChunkId(127))
            val result = testInstance.nextChunkId(ConsumerId("different-group-id"), TopicId(1))
            assertThat(result, equalTo(ChunkId(0)))
            assertThat(testInstance.count(), equalTo(1UL))
        }

        private val testInstance = MemoryBrokerConsumersRepository()
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
            testInstance.commit(ConsumerId("some-group"), TopicId(123), ChunkId(23))
            assertThat(testInstance.count(), equalTo(1UL))
        }

        @Test
        fun `Committed before`() {
            testInstance.commit(ConsumerId("some-group"), TopicId(123), ChunkId(23))

            testInstance.commit(ConsumerId("some-group"), TopicId(123), ChunkId(1337))
            assertThat(testInstance.count(), equalTo(1UL))
        }

        @Test
        fun `Does not overwrite different topic id `() {
            testInstance.commit(ConsumerId("some-group"), TopicId(23), ChunkId(1))
            testInstance.commit(ConsumerId("some-group"), TopicId(34), ChunkId(2))

            assertThat(testInstance.count(), equalTo(2UL))
        }

        @Test
        fun `Does not overwrite different group id`() {
            testInstance.commit(ConsumerId("some-group"), TopicId(23), ChunkId(1))
            testInstance.commit(ConsumerId("another-group"), TopicId(23), ChunkId(2))

            assertThat(testInstance.count(), equalTo(2UL))
        }

        private val testInstance = MemoryBrokerConsumersRepository()
    }
}