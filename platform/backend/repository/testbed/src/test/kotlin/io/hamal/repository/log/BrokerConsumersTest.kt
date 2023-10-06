package io.hamal.repository.log

import io.hamal.lib.domain.vo.TopicId
import io.hamal.repository.api.log.BrokerConsumersRepository
import io.hamal.repository.api.log.ChunkId
import io.hamal.repository.api.log.ConsumerId
import io.hamal.repository.fixture.AbstractUnitTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory


class BrokerConsumersRepositoryTest : AbstractUnitTest() {

    @Nested
    inner class NextChunkTopicIdTest {

        @TestFactory
        fun `Returns chunk id 0 if no entry exists for consumer id and topic id`() =
            runWith(BrokerConsumersRepository::class) {
                val result = nextChunkId(ConsumerId("some-consumer-id"), TopicId(42))
                assertThat(result, equalTo(ChunkId(0)))
                assertThat(count(), equalTo(0UL))
            }

        @TestFactory
        fun `Next chunk id - is last committed chunk id plus 1`() =
            runWith(BrokerConsumersRepository::class) {
                commit(ConsumerId("some-consumer-id"), TopicId(1), ChunkId(127))
                val result = nextChunkId(ConsumerId("some-consumer-id"), TopicId(1))
                assertThat(result, equalTo(ChunkId(128)))
                assertThat(count(), equalTo(1UL))
            }

        @TestFactory
        fun `Does not return next chunk id of different topic`() =
            runWith(BrokerConsumersRepository::class) {
                commit(ConsumerId("some-consumer-id"), TopicId(1), ChunkId(127))
                val result = nextChunkId(ConsumerId("some-consumer-id"), TopicId(2))
                assertThat(result, equalTo(ChunkId(0)))
                assertThat(count(), equalTo(1UL))
            }

        @TestFactory
        fun `Does not return next chunk id of different consumer`() =
            runWith(BrokerConsumersRepository::class) {
                commit(ConsumerId("some-consumer-id"), TopicId(1), ChunkId(127))
                val result = nextChunkId(ConsumerId("different-consumer-id"), TopicId(1))
                assertThat(result, equalTo(ChunkId(0)))
                assertThat(count(), equalTo(1UL))
            }

    }

    @Nested
    inner class CommitTest {

        @TestFactory
        fun `Committed before`() =
            runWith(BrokerConsumersRepository::class) {
                commit(ConsumerId("some-consumer"), TopicId(123), ChunkId(23))
                commit(ConsumerId("some-consumer"), TopicId(123), ChunkId(1337))
                assertThat(count(), equalTo(1UL))
            }

        @TestFactory
        fun `Does not overwrite different topic id `() =
            runWith(BrokerConsumersRepository::class) {
                commit(ConsumerId("some-consumer"), TopicId(23), ChunkId(1))
                commit(ConsumerId("some-consumer"), TopicId(34), ChunkId(2))
                assertThat(count(), equalTo(2UL))
            }


        @TestFactory
        fun `Does not overwrite different consumer id `() =
            runWith(BrokerConsumersRepository::class) {
                commit(ConsumerId("some-consumer"), TopicId(23), ChunkId(1))
                commit(ConsumerId("another-consumer"), TopicId(23), ChunkId(2))

                assertThat(count(), equalTo(2UL))
            }

    }
}