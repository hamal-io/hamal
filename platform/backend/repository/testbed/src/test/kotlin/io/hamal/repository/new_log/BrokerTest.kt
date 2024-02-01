package io.hamal.repository.new_log

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.repository.api.new_log.*
import io.hamal.repository.api.new_log.LogTopicCreate.ToCreate
import io.hamal.repository.fixture.AbstractUnitTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

internal class LogBrokerRepositoryTest : AbstractUnitTest() {

    @Nested
    inner class LogTopicCreateTest {
        @TestFactory
        fun `Creates a new topic`() = runWith(LogBrokerRepository::class) {
            val result = create(
                CmdId(1), ToCreate(
                    id = LogTopicId(1),
                    name = LogTopicName("very-first-topic"),
                    groupId = LogTopicGroupId(234)
                )
            )

            assertThat(result.id, equalTo(LogTopicId(1)))
            assertThat(result.name, equalTo(LogTopicName("very-first-topic")))
            assertThat(result.groupId, equalTo(LogTopicGroupId(234)))

            assertThat(count(LogTopicQuery(groupIds = listOf())), equalTo(1UL))
        }

        @TestFactory
        fun `Bug - able to create realistic topic name`() = runWith(LogBrokerRepository::class) {
            create(
                CmdId(1),
                ToCreate(
                    id = LogTopicId(1),
                    name = LogTopicName("very-first-topic"),
                    groupId = LogTopicGroupId(345)
                )
            )

            val result = create(
                CmdId(2),
                ToCreate(LogTopicId(2), LogTopicName("func::created"), LogTopicGroupId(345))
            )
            assertThat(result.id, equalTo(LogTopicId(2)))
            assertThat(result.name, equalTo(LogTopicName("func::created")))
            assertThat(result.groupId, equalTo(LogTopicGroupId(345)))

            assertThat(count(LogTopicQuery(groupIds = listOf())), equalTo(2UL))
        }

        @TestFactory
        fun `Does not create a new topic if topic already exists with same group id`() =
            runWith(LogBrokerRepository::class) {
                create(
                    CmdId(1),
                    ToCreate(
                        id = LogTopicId(1),
                        name = LogTopicName("very-first-topic"),
                        groupId = LogTopicGroupId(1)
                    )
                )

                assertThrows<IllegalArgumentException> {
                    create(
                        CmdId(2),
                        ToCreate(
                            id = LogTopicId(2),
                            name = LogTopicName("very-first-topic"),
                            groupId = LogTopicGroupId(1)
                        )
                    )
                }.also { exception ->
                    assertThat(exception.message, equalTo("Topic already exists"))
                }

                assertThat(count(LogTopicQuery(groupIds = listOf())), equalTo(1UL))
            }

        @TestFactory
        fun `Create a new topic even if topic name already exists for different group id`() =
            runWith(LogBrokerRepository::class) {
                create(
                    CmdId(1),
                    ToCreate(
                        id = LogTopicId(1),
                        name = LogTopicName("very-first-topic"),
                        groupId = LogTopicGroupId(1)
                    )
                )

                create(
                    CmdId(2),
                    ToCreate(
                        id = LogTopicId(2),
                        name = LogTopicName("very-first-topic"),
                        groupId = LogTopicGroupId(2)
                    )
                )

                create(
                    CmdId(3),
                    ToCreate(
                        id = LogTopicId(3),
                        name = LogTopicName("very-first-topic"),
                        groupId = LogTopicGroupId(3)
                    )
                )

                assertThat(count(LogTopicQuery(groupIds = listOf())), equalTo(3UL))
            }

        @TestFactory
        fun `Topic name only exists for different group id`() = runWith(LogBrokerRepository::class) {
            setupTopic()

            create(
                CmdId(123), ToCreate(
                    id = LogTopicId(2345),
                    name = LogTopicName("created-topic"),
                    groupId = LogTopicGroupId(2)
                )
            )

            assertThat(count(LogTopicQuery(groupIds = listOf())), equalTo(2UL))
        }

        @TestFactory
        fun `Bug - Able to resolve real topic`() = runWith(LogBrokerRepository::class) {
            val result = create(
                CmdId(123),
                ToCreate(
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
    inner class LogTopicFindTest {
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
    inner class LogTopicListAndCountTest {

        @TestFactory
        fun `List and count on empty repository`() = runWith(LogBrokerRepository::class) {
            val query = LogTopicQuery()

            val resultList = list(query)
            assertThat(resultList, equalTo(listOf()))

            val resultCount = count(query)
            assertThat(resultCount, equalTo(0UL))
        }

        @TestFactory
        fun `List and count all topics`() = runWith(LogBrokerRepository::class) {
            setupTopics()
            val query = LogTopicQuery(limit = Limit(10))

            val resultList = list(query)
            assertThat(resultList, hasSize(9))

            assertThat(resultList[0].name, equalTo(LogTopicName("topic-nine")))
            assertThat(resultList[4].name, equalTo(LogTopicName("topic-five")))
            assertThat(resultList[8].name, equalTo(LogTopicName("topic-one")))

            val resultCount = count(query)
            assertThat(resultCount, equalTo(9UL))
        }

        @TestFactory
        fun `List and count all group topics`() = runWith(LogBrokerRepository::class) {
            setupTopics()
            val query = LogTopicQuery(groupIds = listOf(LogTopicGroupId(2)), limit = Limit(10))

            val resultList = list(query)
            assertThat(resultList, hasSize(2))

            assertThat(resultList[0].name, equalTo(LogTopicName("topic-eight")))
            assertThat(resultList[1].name, equalTo(LogTopicName("topic-seven")))

            val resultCount = count(query)
            assertThat(resultCount, equalTo(2UL))
        }

        @TestFactory
        fun `Limit list and count`() = runWith(LogBrokerRepository::class) {
            setupTopics()
            val query = LogTopicQuery(limit = Limit(5))

            val resultList = list(query)
            assertThat(resultList, hasSize(5))

            assertThat(resultList[0].name, equalTo(LogTopicName("topic-nine")))
            assertThat(resultList[4].name, equalTo(LogTopicName("topic-five")))

            val resultCount = count(query)
            assertThat(resultCount, equalTo(9UL))
        }

        @TestFactory
        fun `Skip and limit - list and count`() = runWith(LogBrokerRepository::class) {
            setupTopics()
            val query = LogTopicQuery(limit = Limit(1), afterId = LogTopicId(5))

            val resultList = list(query)
            assertThat(resultList, hasSize(1))

            assertThat(resultList[0].name, equalTo(LogTopicName("topic-four")))

            val resultCount = count(query)
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

            val resultList = list(query)
            assertThat(resultList, hasSize(2))

            assertThat(resultList[0].name, equalTo(LogTopicName("topic-eight")))
            assertThat(resultList[1].name, equalTo(LogTopicName("topic-five")))

            val resultCount = count(query.apply { limit = Limit(1) })
            assertThat(resultCount, equalTo(2UL))
        }

    }

    private fun LogBrokerRepository.setupTopic() {
        create(
            CmdId(1), ToCreate(
                id = LogTopicId(5432),
                name = LogTopicName("created-topic"),
                groupId = LogTopicGroupId(1)
            )
        )
    }

    private fun LogBrokerRepository.setupTopics() {
        create(
            CmdId(1),
            ToCreate(LogTopicId(1), LogTopicName("topic-one"), LogTopicGroupId(1))
        )
        create(
            CmdId(2),
            ToCreate(LogTopicId(2), LogTopicName("topic-two"), LogTopicGroupId(1))
        )
        create(
            CmdId(3),
            ToCreate(LogTopicId(3), LogTopicName("topic-three"), LogTopicGroupId(1))
        )
        create(
            CmdId(4),
            ToCreate(LogTopicId(4), LogTopicName("topic-four"), LogTopicGroupId(1))
        )
        create(
            CmdId(5),
            ToCreate(LogTopicId(5), LogTopicName("topic-five"), LogTopicGroupId(1))
        )
        create(
            CmdId(6),
            ToCreate(LogTopicId(6), LogTopicName("topic-six"), LogTopicGroupId(1))
        )
        create(
            CmdId(7),
            ToCreate(LogTopicId(7), LogTopicName("topic-seven"), LogTopicGroupId(2))
        )
        create(
            CmdId(8),
            ToCreate(LogTopicId(8), LogTopicName("topic-eight"), LogTopicGroupId(2))
        )
        create(
            CmdId(9),
            ToCreate(LogTopicId(9), LogTopicName("topic-nine"), LogTopicGroupId(3))
        )
    }
}
