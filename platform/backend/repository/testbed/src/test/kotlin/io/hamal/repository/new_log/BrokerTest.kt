package io.hamal.repository.new_log

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.LogTopicId
import io.hamal.repository.api.new_log.LogBrokerRepository
import io.hamal.repository.api.new_log.LogBrokerRepository.*
import io.hamal.repository.api.new_log.LogConsumerId
import io.hamal.repository.api.new_log.LogEntryId
import io.hamal.repository.fixture.AbstractUnitTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import java.math.BigInteger

internal class LogBrokerRepositoryTest : AbstractUnitTest() {

    @Nested
    inner class CreateTopicTest {

        @TestFactory
        fun `Creates a new topic`() = runWith(LogBrokerRepository::class) {
            val result = create(CmdId(1), LogTopicToCreate(LogTopicId(1)))
            assertThat(result.id, equalTo(LogTopicId(1)))

            assertThat(countTopics(LogTopicQuery()), equalTo(1UL))
        }

    }

    @Nested
    inner class FindTopicTest {
        @TestFactory
        fun `Topic found by id`() = runWith(LogBrokerRepository::class) {
            setupTopic()

            with(findTopic(LogTopicId(5432))!!) {
                assertThat(id, equalTo(LogTopicId(5432)))
            }
        }

        @TestFactory
        fun `Topic not found by id`() = runWith(LogBrokerRepository::class) {
            setupTopic()

            val result = findTopic(LogTopicId(1234))
            assertThat(result, nullValue())
        }
    }

    @Nested
    inner class GetTopicTest {
        @TestFactory
        fun `Topic found by id`() = runWith(LogBrokerRepository::class) {
            setupTopic()

            with(getTopic(LogTopicId(5432))) {
                assertThat(id, equalTo(LogTopicId(5432)))
            }
        }

        @TestFactory
        fun `Topic not found by id`() = runWith(LogBrokerRepository::class) {
            setupTopic()

            assertThrows<NoSuchElementException> {
                getTopic(LogTopicId(1234))
            }.also { exception ->
                assertThat(exception.message, equalTo("Topic not found"))
            }
        }
    }

    @Nested
    inner class ListAndCountTopicsTest {

        @TestFactory
        fun `List and count on empty repository`() = runWith(LogBrokerRepository::class) {
            val query = LogTopicQuery()

            val resultList = listTopics(query)
            assertThat(resultList, equalTo(listOf()))

            val resultCount = countTopics(query)
            assertThat(resultCount, equalTo(0UL))
        }

        @TestFactory
        fun `List and count all topics`() = runWith(LogBrokerRepository::class) {
            setupTopics()
            val query = LogTopicQuery(limit = Limit(10))

            val resultList = listTopics(query)
            assertThat(resultList, hasSize(9))

            assertThat(resultList[0].id, equalTo(LogTopicId(9)))
            assertThat(resultList[4].id, equalTo(LogTopicId(5)))
            assertThat(resultList[8].id, equalTo(LogTopicId(1)))

            val resultCount = countTopics(query)
            assertThat(resultCount, equalTo(9UL))
        }

        @TestFactory
        fun `Limit list and count`() = runWith(LogBrokerRepository::class) {
            setupTopics()
            val query = LogTopicQuery(limit = Limit(5))

            val resultList = listTopics(query)
            assertThat(resultList, hasSize(5))

            assertThat(resultList[0].id, equalTo(LogTopicId(9)))
            assertThat(resultList[4].id, equalTo(LogTopicId(5)))

            val resultCount = countTopics(query)
            assertThat(resultCount, equalTo(9UL))
        }

        @TestFactory
        fun `Skip and limit - list and count`() = runWith(LogBrokerRepository::class) {
            setupTopics()
            val query = LogTopicQuery(limit = Limit(1), afterId = LogTopicId(5))

            val resultList = listTopics(query)
            assertThat(resultList, hasSize(1))

            assertThat(resultList[0].id, equalTo(LogTopicId(4)))

            val resultCount = countTopics(query)
            assertThat(resultCount, equalTo(4UL))
        }

    }

    @Nested
    inner class CommitTest {

        @TestFactory
        fun `Committed before`() =
            runWith(LogBrokerRepository::class) {
                commit(LogConsumerId(1), LogTopicId(123), LogEntryId(23))
                commit(LogConsumerId(1), LogTopicId(123), LogEntryId(1337))

                assertThat(countConsumers(LogConsumerQuery()), equalTo(1UL))
            }

        @TestFactory
        fun `Does not overwrite different topic id `() =
            runWith(LogBrokerRepository::class) {
                commit(LogConsumerId(1), LogTopicId(23), LogEntryId(1))
                commit(LogConsumerId(1), LogTopicId(34), LogEntryId(2))

                assertThat(countConsumers(LogConsumerQuery()), equalTo(2UL))
            }


        @TestFactory
        fun `Does not overwrite different consumer id `() =
            runWith(LogBrokerRepository::class) {
                commit(LogConsumerId(1), LogTopicId(23), LogEntryId(1))
                commit(LogConsumerId(2), LogTopicId(23), LogEntryId(2))

                assertThat(countConsumers(LogConsumerQuery()), equalTo(2UL))
            }

    }

    @Nested
    inner class AppendTest {
        @TestFactory
        fun `Append value to topic with long cmd id`() =
            runWith(LogBrokerRepository::class) {
                val topic = create(
                    cmdId = CmdId(1),
                    topicToCreate = LogTopicToCreate(LogTopicId(2))
                )

                append(
                    CmdId(BigInteger("380896718712995851145215087")),
                    topic.id,
                    "some-content-1".toByteArray()
                )

                append(
                    CmdId(BigInteger("380896718712995851145215088")),
                    topic.id,
                    "some-content-2".toByteArray()
                )

                val result = consume(LogConsumerId(1), topic.id, Limit(1_000))
                assertThat(result, hasSize(2))
                assertThat(result[0].id, equalTo(LogEntryId(1)))
                assertThat(result[0].topicId, equalTo(LogTopicId(2)))
                assertThat(result[0].bytes, equalTo("some-content-1".toByteArray()))

                assertThat(result[1].id, equalTo(LogEntryId(2)))
                assertThat(result[1].topicId, equalTo(LogTopicId(2)))
                assertThat(result[1].bytes, equalTo("some-content-2".toByteArray()))
            }
    }
}

private fun LogBrokerRepository.setupTopic() {
    create(CmdId(1), LogTopicToCreate(LogTopicId(5432)))
}

private fun LogBrokerRepository.setupTopics() {
    create(CmdId(1), LogTopicToCreate(LogTopicId(1)))
    create(CmdId(2), LogTopicToCreate(LogTopicId(2)))
    create(CmdId(3), LogTopicToCreate(LogTopicId(3)))
    create(CmdId(4), LogTopicToCreate(LogTopicId(4)))
    create(CmdId(5), LogTopicToCreate(LogTopicId(5)))
    create(CmdId(6), LogTopicToCreate(LogTopicId(6)))
    create(CmdId(7), LogTopicToCreate(LogTopicId(7)))
    create(CmdId(8), LogTopicToCreate(LogTopicId(8)))
    create(CmdId(9), LogTopicToCreate(LogTopicId(9)))
}

