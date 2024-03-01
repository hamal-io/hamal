package io.hamal.repository.log

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.LogTopicId
import io.hamal.repository.api.log.LogBrokerRepository
import io.hamal.repository.api.log.LogBrokerRepository.*
import io.hamal.repository.api.log.LogConsumerId
import io.hamal.repository.api.log.LogEventId
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
            val result = create(CreateTopicCmd(CmdId(1), LogTopicId(1)))
            assertThat(result.id, equalTo(LogTopicId(1)))

            assertThat(countTopics(LogTopicQuery()), equalTo(Count(1)))
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
            assertThat(resultCount, equalTo(Count(0)))
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
            assertThat(resultCount, equalTo(Count(9)))
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
            assertThat(resultCount, equalTo(Count(9)))
        }

        @TestFactory
        fun `Skip and limit - list and count`() = runWith(LogBrokerRepository::class) {
            setupTopics()
            val query = LogTopicQuery(limit = Limit(1), afterId = LogTopicId(5))

            val resultList = listTopics(query)
            assertThat(resultList, hasSize(1))

            assertThat(resultList[0].id, equalTo(LogTopicId(4)))

            val resultCount = countTopics(query)
            assertThat(resultCount, equalTo(Count(4)))
        }

    }

    @Nested
    inner class CommitTest {

        @TestFactory
        fun `Committed before`() =
            runWith(LogBrokerRepository::class) {
                commit(LogConsumerId(1), LogTopicId(123), LogEventId(23))
                commit(LogConsumerId(1), LogTopicId(123), LogEventId(1337))

                assertThat(countConsumers(LogConsumerQuery()), equalTo(Count(1)))
            }

        @TestFactory
        fun `Does not overwrite different topic id `() =
            runWith(LogBrokerRepository::class) {
                commit(LogConsumerId(1), LogTopicId(23), LogEventId(1))
                commit(LogConsumerId(1), LogTopicId(34), LogEventId(2))

                assertThat(countConsumers(LogConsumerQuery()), equalTo(Count(2)))
            }


        @TestFactory
        fun `Does not overwrite different consumer id `() =
            runWith(LogBrokerRepository::class) {
                commit(LogConsumerId(1), LogTopicId(23), LogEventId(1))
                commit(LogConsumerId(2), LogTopicId(23), LogEventId(2))

                assertThat(countConsumers(LogConsumerQuery()), equalTo(Count(2)))
            }

    }

    @Nested
    inner class AppendTest {
        @TestFactory
        fun `Append value to topic with long cmd id`() =
            runWith(LogBrokerRepository::class) {
                val topic = create(CreateTopicCmd(CmdId(1), LogTopicId(2)))

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
                assertThat(result[0].id, equalTo(LogEventId(1)))
                assertThat(result[0].topicId, equalTo(LogTopicId(2)))
                assertThat(result[0].bytes, equalTo("some-content-1".toByteArray()))

                assertThat(result[1].id, equalTo(LogEventId(2)))
                assertThat(result[1].topicId, equalTo(LogTopicId(2)))
                assertThat(result[1].bytes, equalTo("some-content-2".toByteArray()))
            }
    }
}

private fun LogBrokerRepository.setupTopic() {
    create(CreateTopicCmd(CmdId(1), (LogTopicId(5432))))
}

private fun LogBrokerRepository.setupTopics() {
    create(CreateTopicCmd(CmdId(1), LogTopicId(1)))
    create(CreateTopicCmd(CmdId(2), LogTopicId(2)))
    create(CreateTopicCmd(CmdId(3), LogTopicId(3)))
    create(CreateTopicCmd(CmdId(4), LogTopicId(4)))
    create(CreateTopicCmd(CmdId(5), LogTopicId(5)))
    create(CreateTopicCmd(CmdId(6), LogTopicId(6)))
    create(CreateTopicCmd(CmdId(7), LogTopicId(7)))
    create(CreateTopicCmd(CmdId(8), LogTopicId(8)))
    create(CreateTopicCmd(CmdId(9), LogTopicId(9)))
}

