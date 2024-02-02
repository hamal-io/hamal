package io.hamal.repository.new_log

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.repository.api.new_log.*
import io.hamal.repository.api.new_log.LogBrokerRepository.*
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
            val result = create(
                CmdId(1), LogTopicToCreate(
                    id = LogTopicId(1),
                    name = LogTopicName("very-first-topic"),
                    groupId = LogTopicGroupId(234)
                )
            )

            assertThat(result.id, equalTo(LogTopicId(1)))
            assertThat(result.name, equalTo(LogTopicName("very-first-topic")))
            assertThat(result.groupId, equalTo(LogTopicGroupId(234)))

            assertThat(countTopics(LogTopicQuery(groupIds = listOf())), equalTo(1UL))
        }

        @TestFactory
        fun `Bug - able to create realistic topic name`() = runWith(LogBrokerRepository::class) {
            create(
                CmdId(1),
                LogTopicToCreate(
                    id = LogTopicId(1),
                    name = LogTopicName("very-first-topic"),
                    groupId = LogTopicGroupId(345)
                )
            )

            val result = create(
                CmdId(2),
                LogTopicToCreate(LogTopicId(2), LogTopicName("func::created"), LogTopicGroupId(345))
            )
            assertThat(result.id, equalTo(LogTopicId(2)))
            assertThat(result.name, equalTo(LogTopicName("func::created")))
            assertThat(result.groupId, equalTo(LogTopicGroupId(345)))

            assertThat(countTopics(LogTopicQuery(groupIds = listOf())), equalTo(2UL))
        }

        @TestFactory
        fun `Does not create a new topic if topic already exists with same group id`() =
            runWith(LogBrokerRepository::class) {
                create(
                    CmdId(1),
                    LogTopicToCreate(
                        id = LogTopicId(1),
                        name = LogTopicName("very-first-topic"),
                        groupId = LogTopicGroupId(1)
                    )
                )

                assertThrows<IllegalArgumentException> {
                    create(
                        CmdId(2),
                        LogTopicToCreate(
                            id = LogTopicId(2),
                            name = LogTopicName("very-first-topic"),
                            groupId = LogTopicGroupId(1)
                        )
                    )
                }.also { exception ->
                    assertThat(exception.message, equalTo("Topic already exists"))
                }

                assertThat(countTopics(LogTopicQuery(groupIds = listOf())), equalTo(1UL))
            }

        @TestFactory
        fun `Create a new topic even if topic name already exists for different group id`() =
            runWith(LogBrokerRepository::class) {
                create(
                    CmdId(1),
                    LogTopicToCreate(
                        id = LogTopicId(1),
                        name = LogTopicName("very-first-topic"),
                        groupId = LogTopicGroupId(1)
                    )
                )

                create(
                    CmdId(2),
                    LogTopicToCreate(
                        id = LogTopicId(2),
                        name = LogTopicName("very-first-topic"),
                        groupId = LogTopicGroupId(2)
                    )
                )

                create(
                    CmdId(3),
                    LogTopicToCreate(
                        id = LogTopicId(3),
                        name = LogTopicName("very-first-topic"),
                        groupId = LogTopicGroupId(3)
                    )
                )

                assertThat(countTopics(LogTopicQuery(groupIds = listOf())), equalTo(3UL))
            }

        @TestFactory
        fun `Topic name only exists for different group id`() = runWith(LogBrokerRepository::class) {
            setupTopic()

            create(
                CmdId(123), LogTopicToCreate(
                    id = LogTopicId(2345),
                    name = LogTopicName("created-topic"),
                    groupId = LogTopicGroupId(2)
                )
            )

            assertThat(countTopics(LogTopicQuery(groupIds = listOf())), equalTo(2UL))
        }

        @TestFactory
        fun `Bug - Able to resolve real topic`() = runWith(LogBrokerRepository::class) {
            val result = create(
                CmdId(123),
                LogTopicToCreate(
                    id = LogTopicId(234),
                    name = LogTopicName("scheduler::flow_enqueued"),
                    groupId = LogTopicGroupId(1)
                )
            )

            assertThat(result.id, equalTo(LogTopicId(234)))
            assertThat(result.name, equalTo(LogTopicName("scheduler::flow_enqueued")))
            assertThat(result.groupId, equalTo(LogTopicGroupId(1)))
        }

    }

    @Nested
    inner class FindTopicTest {
        @TestFactory
        fun `Topic found by id`() = runWith(LogBrokerRepository::class) {
            setupTopic()

            with(findTopic(LogTopicId(5432))!!) {
                assertThat(id, equalTo(LogTopicId(5432)))
                assertThat(name, equalTo(LogTopicName("created-topic")))
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
                assertThat(name, equalTo(LogTopicName("created-topic")))
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

            assertThat(resultList[0].name, equalTo(LogTopicName("topic-nine")))
            assertThat(resultList[4].name, equalTo(LogTopicName("topic-five")))
            assertThat(resultList[8].name, equalTo(LogTopicName("topic-one")))

            val resultCount = countTopics(query)
            assertThat(resultCount, equalTo(9UL))
        }

        @TestFactory
        fun `List and count all group topics`() = runWith(LogBrokerRepository::class) {
            setupTopics()
            val query = LogTopicQuery(groupIds = listOf(LogTopicGroupId(2)), limit = Limit(10))

            val resultList = listTopics(query)
            assertThat(resultList, hasSize(2))

            assertThat(resultList[0].name, equalTo(LogTopicName("topic-eight")))
            assertThat(resultList[1].name, equalTo(LogTopicName("topic-seven")))

            val resultCount = countTopics(query)
            assertThat(resultCount, equalTo(2UL))
        }

        @TestFactory
        fun `Limit list and count`() = runWith(LogBrokerRepository::class) {
            setupTopics()
            val query = LogTopicQuery(limit = Limit(5))

            val resultList = listTopics(query)
            assertThat(resultList, hasSize(5))

            assertThat(resultList[0].name, equalTo(LogTopicName("topic-nine")))
            assertThat(resultList[4].name, equalTo(LogTopicName("topic-five")))

            val resultCount = countTopics(query)
            assertThat(resultCount, equalTo(9UL))
        }

        @TestFactory
        fun `Skip and limit - list and count`() = runWith(LogBrokerRepository::class) {
            setupTopics()
            val query = LogTopicQuery(limit = Limit(1), afterId = LogTopicId(5))

            val resultList = listTopics(query)
            assertThat(resultList, hasSize(1))

            assertThat(resultList[0].name, equalTo(LogTopicName("topic-four")))

            val resultCount = countTopics(query)
            assertThat(resultCount, equalTo(4UL))
        }

        @TestFactory
        fun `List and count by providing topic names`() = runWith(LogBrokerRepository::class) {
            setupTopics()

            val query = LogTopicQuery(
                groupIds = listOf(),
                limit = Limit(10),
                names = listOf(
                    LogTopicName("topic-five"),
                    LogTopicName("topic-eight"),
                    LogTopicName("topic-ten")
                )
            )

            val resultList = listTopics(query)
            assertThat(resultList, hasSize(2))

            assertThat(resultList[0].name, equalTo(LogTopicName("topic-eight")))
            assertThat(resultList[1].name, equalTo(LogTopicName("topic-five")))

            val resultCount = countTopics(query.apply { limit = Limit(1) })
            assertThat(resultCount, equalTo(2UL))
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
                    topicToCreate = LogTopicToCreate(
                        id = LogTopicId(2),
                        name = LogTopicName("test-topic"),
                        groupId = LogTopicGroupId(3)
                    )
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
    create(
        CmdId(1), LogTopicToCreate(
            id = LogTopicId(5432),
            name = LogTopicName("created-topic"),
            groupId = LogTopicGroupId(1)
        )
    )
}

private fun LogBrokerRepository.setupTopics() {
    create(
        CmdId(1),
        LogTopicToCreate(LogTopicId(1), LogTopicName("topic-one"), LogTopicGroupId(1))
    )
    create(
        CmdId(2),
        LogTopicToCreate(LogTopicId(2), LogTopicName("topic-two"), LogTopicGroupId(1))
    )
    create(
        CmdId(3),
        LogTopicToCreate(LogTopicId(3), LogTopicName("topic-three"), LogTopicGroupId(1))
    )
    create(
        CmdId(4),
        LogTopicToCreate(LogTopicId(4), LogTopicName("topic-four"), LogTopicGroupId(1))
    )
    create(
        CmdId(5),
        LogTopicToCreate(LogTopicId(5), LogTopicName("topic-five"), LogTopicGroupId(1))
    )
    create(
        CmdId(6),
        LogTopicToCreate(LogTopicId(6), LogTopicName("topic-six"), LogTopicGroupId(1))
    )
    create(
        CmdId(7),
        LogTopicToCreate(LogTopicId(7), LogTopicName("topic-seven"), LogTopicGroupId(2))
    )
    create(
        CmdId(8),
        LogTopicToCreate(LogTopicId(8), LogTopicName("topic-eight"), LogTopicGroupId(2))
    )
    create(
        CmdId(9),
        LogTopicToCreate(LogTopicId(9), LogTopicName("topic-nine"), LogTopicGroupId(3))
    )
}

