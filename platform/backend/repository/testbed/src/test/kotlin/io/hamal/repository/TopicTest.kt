package io.hamal.repository

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain._enum.TopicType
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.Topic
import io.hamal.repository.api.TopicCmdRepository.TopicFlowCreateCmd
import io.hamal.repository.api.TopicCmdRepository.TopicInternalCreateCmd
import io.hamal.repository.api.TopicQueryRepository.TopicQuery
import io.hamal.repository.api.TopicRepository
import io.hamal.repository.fixture.AbstractUnitTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import kotlin.math.abs
import kotlin.random.Random

internal class TopicRepositoryTest : AbstractUnitTest() {

    @Nested
    inner class CreateTopicFlow {

        @TestFactory
        fun `Creates flow topic`() = runWith(TopicRepository::class) {
            val result = create(
                TopicFlowCreateCmd(
                    id = CmdId(1),
                    topicId = TopicId(2),
                    logTopicId = LogTopicId(3),
                    groupId = GroupId(4),
                    flowId = FlowId(5),
                    name = TopicName("topic-name"),
                )
            )

            with(result) {
                assertThat(id, equalTo(TopicId(2)))
                assertThat(logTopicId, equalTo(LogTopicId(3)))
                assertThat(groupId, equalTo(GroupId(4)))
                assertThat(flowId, equalTo(FlowId(5)))
                assertThat(name, equalTo(TopicName("topic-name")))
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Tries to create but same name already exists in group`() = runWith(TopicRepository::class) {
            createFlowTopic(
                topicId = TopicId(1),
                flowId = FlowId(2),
                groupId = GroupId(3),
                logTopicId = LogTopicId(4),
                name = TopicName("topic-name")
            )

            assertThrows<IllegalArgumentException> {
                create(
                    TopicFlowCreateCmd(
                        id = CmdId(10),
                        topicId = TopicId(11),
                        logTopicId = LogTopicId(12),
                        flowId = FlowId(14),
                        groupId = GroupId(3),
                        name = TopicName("topic-name"),
                    )
                )
            }.also { exception ->
                assertThat(
                    exception.message, equalTo("TopicName(topic-name) already exists")
                )
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Creates with same name but different group`() = runWith(TopicRepository::class) {
            createFlowTopic(
                topicId = TopicId(1), flowId = FlowId(2), groupId = GroupId(3), name = TopicName("first-topic-name")
            )

            create(
                TopicFlowCreateCmd(
                    id = CmdId(10),
                    topicId = TopicId(11),
                    logTopicId = LogTopicId(12),
                    flowId = FlowId(13),
                    groupId = GroupId(14),
                    name = TopicName("first-topic-name"),
                )
            )

            verifyCount(2)
        }

        @TestFactory
        fun `Tries to create but cmd with trigger id was already applied`() =
            runWith(TopicRepository::class) {

                createFlowTopic(
                    cmdId = CmdId(23456),
                    topicId = TopicId(1),
                    flowId = FlowId(2),
                    groupId = GroupId(3),
                    logTopicId = LogTopicId(4),
                    name = TopicName("first-topic")
                )

                val result = create(
                    TopicFlowCreateCmd(
                        id = CmdId(23456),
                        topicId = TopicId(1),
                        groupId = GroupId(333),
                        flowId = FlowId(2222),
                        logTopicId = LogTopicId(4444),
                        name = TopicName("second-topic")
                    )
                )

                with(result) {
                    assertThat(id, equalTo(TopicId(1)))
                    assertThat(logTopicId, equalTo(LogTopicId(4)))
                    assertThat(groupId, equalTo(GroupId(3)))
                    assertThat(flowId, equalTo(FlowId(2)))
                    assertThat(name, equalTo(TopicName("first-topic")))
                }

                verifyCount(1)
            }

    }

    @Nested
    inner class CreateTopicInternal {

        @TestFactory
        fun `Creates internal topic`() = runWith(TopicRepository::class) {
            val result = create(
                TopicInternalCreateCmd(
                    id = CmdId(1),
                    topicId = TopicId(2),
                    logTopicId = LogTopicId(3),
                    name = TopicName("topic-name"),
                )
            )

            with(result) {
                assertThat(id, equalTo(TopicId(2)))
                assertThat(logTopicId, equalTo(LogTopicId(3)))
                assertThat(groupId, equalTo(GroupId.root))
                assertThat(name, equalTo(TopicName("topic-name")))
            }
            verifyCount(1)
        }

        @TestFactory
        fun `Tries to create but same name already exists in group`() = runWith(TopicRepository::class) {
            createInternalTopic(
                topicId = TopicId(1),
                logTopicId = LogTopicId(2),
                name = TopicName("topic-name")
            )

            assertThrows<IllegalArgumentException> {
                create(
                    TopicInternalCreateCmd(
                        id = CmdId(10),
                        topicId = TopicId(11),
                        logTopicId = LogTopicId(12),
                        name = TopicName("topic-name"),
                    )
                )
            }.also { exception ->
                assertThat(
                    exception.message, equalTo("TopicName(topic-name) already exists")
                )
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Tries to create but cmd with trigger id was already applied`() =
            runWith(TopicRepository::class) {

                createInternalTopic(
                    cmdId = CmdId(23456),
                    topicId = TopicId(1),
                    logTopicId = LogTopicId(4),
                    name = TopicName("first-topic")
                )

                val result = create(
                    TopicInternalCreateCmd(
                        id = CmdId(23456),
                        topicId = TopicId(1),
                        logTopicId = LogTopicId(4444),
                        name = TopicName("second-topic")
                    )
                )

                with(result) {
                    assertThat(id, equalTo(TopicId(1)))
                    assertThat(logTopicId, equalTo(LogTopicId(4)))
                    assertThat(groupId, equalTo(GroupId.root))
                    assertThat(name, equalTo(TopicName("first-topic")))
                }

                verifyCount(1)
            }
    }

    @Nested
    inner class ClearTest {

        @TestFactory
        fun `Nothing to clear`() = runWith(TopicRepository::class) {
            clear()
            verifyCount(0)
        }

        @TestFactory
        fun `Clear table`() = runWith(TopicRepository::class) {
            createFlowTopic(
                cmdId = CmdId(1),
                topicId = TopicId(1),
                flowId = FlowId(2),
                groupId = GroupId(3),
                logTopicId = LogTopicId(4),
                name = TopicName("first-topic-name")
            )

            createFlowTopic(
                cmdId = CmdId(2),
                topicId = TopicId(20),
                flowId = FlowId(22),
                groupId = GroupId(23),
                logTopicId = LogTopicId(5),
                name = TopicName("another-topic-name")
            )

            clear()
            verifyCount(0)
        }

    }

    @Nested
    inner class GetTest {

        @TestFactory
        fun `Get topic by id`() = runWith(TopicRepository::class) {
            createFlowTopic(
                topicId = TopicId(1),
                flowId = FlowId(2),
                groupId = GroupId(3),
                logTopicId = LogTopicId(4),
                name = TopicName("SomeTopic")
            )

            with(get(TopicId(1))) {
                require(this is Topic.Flow)

                assertThat(id, equalTo(TopicId(1)))
                assertThat(logTopicId, equalTo(LogTopicId(4)))
                assertThat(groupId, equalTo(GroupId(3)))
                assertThat(flowId, equalTo(FlowId(2)))
                assertThat(name, equalTo(TopicName("SomeTopic")))
            }
        }

        @TestFactory
        fun `Tries to get topic by id but does not exist`() = runWith(TopicRepository::class) {
            createFlowTopic(
                topicId = TopicId(1),
                flowId = FlowId(2),
                groupId = GroupId(3),
                logTopicId = LogTopicId(4),
                name = TopicName("SomeTopic")
            )

            assertThrows<NoSuchElementException> {
                get(TopicId(111111))
            }.also { exception ->
                assertThat(exception.message, equalTo("Topic not found"))
            }
        }
    }

    @Nested
    inner class FindTest {
        @TestFactory
        fun `Find topic by id`() = runWith(TopicRepository::class) {
            createFlowTopic(
                topicId = TopicId(1),
                flowId = FlowId(2),
                groupId = GroupId(3),
                logTopicId = LogTopicId(4),
                name = TopicName("SomeTopic")
            )

            with(find(TopicId(1))) {
                require(this is Topic.Flow)

                assertThat(id, equalTo(TopicId(1)))
                assertThat(logTopicId, equalTo(LogTopicId(4)))
                assertThat(groupId, equalTo(GroupId(3)))
                assertThat(flowId, equalTo(FlowId(2)))
                assertThat(name, equalTo(TopicName("SomeTopic")))
            }
        }

        @TestFactory
        fun `Tries to find topic by id but does not exist`() = runWith(TopicRepository::class) {
            createFlowTopic(
                topicId = TopicId(1),
                flowId = FlowId(2),
                groupId = GroupId(3),
                logTopicId = LogTopicId(4),
                name = TopicName("SomeTopic")
            )
            val result = find(TopicId(111111))
            assertThat(result, nullValue())
        }
    }

    @Nested
    inner class GetGroupTopic {

        @TestFactory
        fun `Get topic by group id and name`() = runWith(TopicRepository::class) {
            createFlowTopic(
                topicId = TopicId(1),
                flowId = FlowId(2),
                groupId = GroupId(3),
                logTopicId = LogTopicId(4),
                name = TopicName("SomeTopic")
            )

            with(getGroupTopic(GroupId(3), TopicName("SomeTopic"))) {
                require(this is Topic.Flow)

                assertThat(id, equalTo(TopicId(1)))
                assertThat(logTopicId, equalTo(LogTopicId(4)))
                assertThat(groupId, equalTo(GroupId(3)))
                assertThat(flowId, equalTo(FlowId(2)))
                assertThat(name, equalTo(TopicName("SomeTopic")))
            }
        }

        @TestFactory
        fun `Tries to get topic by group id and name but does not exist`() = runWith(TopicRepository::class) {
            createFlowTopic(
                cmdId = CmdId(1),
                topicId = TopicId(1),
                flowId = FlowId(2),
                groupId = GroupId(3),
                logTopicId = LogTopicId(4),
                name = TopicName("SomeTopic")
            )

            assertThrows<NoSuchElementException> {
                getGroupTopic(GroupId(33333333), TopicName("SomeTopic"))
            }.also { exception ->
                assertThat(exception.message, equalTo("Topic not found"))
            }

            assertThrows<NoSuchElementException> {
                getGroupTopic(GroupId(3), TopicName("AnotherTopic"))
            }.also { exception ->
                assertThat(exception.message, equalTo("Topic not found"))
            }
        }

    }

    @Nested
    inner class FindGroupTopicTest {

        @TestFactory
        fun `Finds topic by group id and name`() = runWith(TopicRepository::class) {
            createFlowTopic(
                topicId = TopicId(1),
                flowId = FlowId(2),
                groupId = GroupId(3),
                logTopicId = LogTopicId(4),
                name = TopicName("SomeTopic")
            )

            with(findGroupTopic(GroupId(3), TopicName("SomeTopic"))) {
                require(this is Topic.Flow)

                assertThat(id, equalTo(TopicId(1)))
                assertThat(logTopicId, equalTo(LogTopicId(4)))
                assertThat(groupId, equalTo(GroupId(3)))
                assertThat(flowId, equalTo(FlowId(2)))
                assertThat(name, equalTo(TopicName("SomeTopic")))
            }
        }

        @TestFactory
        fun `Tries to find topic by group id and name but does not exist`() = runWith(TopicRepository::class) {
            createFlowTopic(
                cmdId = CmdId(1),
                topicId = TopicId(1),
                flowId = FlowId(2),
                groupId = GroupId(3),
                logTopicId = LogTopicId(4),
                name = TopicName("SomeTopic")
            )

            assertThat(
                findGroupTopic(GroupId(33333333), TopicName("SomeTopic")),
                nullValue()
            )

            assertThat(
                findGroupTopic(GroupId(3), TopicName("AnotherTopic")),
                nullValue()
            )
        }

    }

    @Nested
    inner class ListAndCountTest {

        @TestFactory
        fun `Returns correct data`() = runWith(TopicRepository::class) {
            setup()

            val query = TopicQuery(
                topicIds = listOf(TopicId(1)),
                limit = Limit.all
            )

            val result = list(query)
            assertThat(result, hasSize(1))

            with(result.first()) {
                require(this is Topic.Flow)
                assertThat(id, equalTo(TopicId(1)))
                assertThat(name, equalTo(TopicName("topic-one")))
                assertThat(flowId, equalTo(FlowId(2)))
                assertThat(groupId, equalTo(GroupId(3)))
                assertThat(logTopicId, equalTo(LogTopicId(4)))
            }
        }

        @TestFactory
        fun `With group ids`() = runWith(TopicRepository::class) {
            setup()

            val query = TopicQuery(
                groupIds = listOf(GroupId(3), GroupId(13)),
                limit = Limit.all
            )

            assertThat(count(query), equalTo(Count(2)))
            val result = list(query)
            assertThat(result, hasSize(2))

            with(result[0]) {
                assertThat(id, equalTo(TopicId(11)))
            }

            with(result[1]) {
                assertThat(id, equalTo(TopicId(1)))
            }
        }

        @TestFactory
        fun `With topic ids`() = runWith(TopicRepository::class) {
            setup()

            val query = TopicQuery(
                topicIds = listOf(TopicId(1), TopicId(21)),
                limit = Limit.all
            )

            assertThat(count(query), equalTo(Count(2)))
            val result = list(query)
            assertThat(result, hasSize(2))

            with(result[0]) {
                assertThat(id, equalTo(TopicId(21)))
            }

            with(result[1]) {
                assertThat(id, equalTo(TopicId(1)))
            }
        }

        @TestFactory
        fun `With flow ids`() = runWith(TopicRepository::class) {
            setup()

            val query = TopicQuery(
                flowIds = listOf(FlowId(2), FlowId(12)),
                limit = Limit.all
            )

            assertThat(count(query), equalTo(Count(2)))
            val result = list(query)
            assertThat(result, hasSize(2))

            with(result[0]) {
                assertThat(id, equalTo(TopicId(11)))
            }

            with(result[1]) {
                assertThat(id, equalTo(TopicId(1)))
            }
        }

        @TestFactory
        fun `With names`() = runWith(TopicRepository::class) {
            setup()

            val query = TopicQuery(
                names = listOf(
                    TopicName("topic-one"),
                    TopicName("topic-three")
                ),
                limit = Limit.all
            )

            assertThat(count(query), equalTo(Count(2)))
            val result = list(query)
            assertThat(result, hasSize(2))

            with(result[0]) {
                assertThat(id, equalTo(TopicId(21)))
            }

            with(result[1]) {
                assertThat(id, equalTo(TopicId(1)))
            }
        }

        @TestFactory
        fun `With types`() = runWith(TopicRepository::class) {
            setup()

            val query = TopicQuery(
                types = listOf(TopicType.Internal),
                limit = Limit.all
            )

            assertThat(count(query), equalTo(Count(1)))
            val result = list(query)
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(TopicId(31)))
            }
        }

        @TestFactory
        fun `Limit`() = runWith(TopicRepository::class) {
            setup()

            val query = TopicQuery(
                limit = Limit(2)
            )

            assertThat(count(query), equalTo(Count(4)))
            val result = list(query)
            assertThat(result, hasSize(2))


            with(result[0]) {
                assertThat(id, equalTo(TopicId(31)))
            }

            with(result[1]) {
                assertThat(id, equalTo(TopicId(21)))
            }
        }

        @TestFactory
        fun `Skip and limit`() = runWith(TopicRepository::class) {
            setup()

            val query = TopicQuery(
                afterId = TopicId(11),
                limit = Limit(1)
            )

            assertThat(count(query), equalTo(Count(1)))
            val result = list(query)
            assertThat(result, hasSize(1))


            with(result[0]) {
                assertThat(id, equalTo(TopicId(1)))
            }
        }


        private fun TopicRepository.setup() {
            createFlowTopic(
                topicId = TopicId(1),
                flowId = FlowId(2),
                groupId = GroupId(3),
                logTopicId = LogTopicId(4),
                name = TopicName("topic-one")
            )

            createFlowTopic(
                topicId = TopicId(11),
                flowId = FlowId(12),
                groupId = GroupId(13),
                logTopicId = LogTopicId(14),
                name = TopicName("topic-two")
            )

            createFlowTopic(
                topicId = TopicId(21),
                flowId = FlowId(22),
                groupId = GroupId(23),
                logTopicId = LogTopicId(24),
                name = TopicName("topic-three")
            )

            createInternalTopic(
                topicId = TopicId(31),
                name = TopicName("topic-four"),
                logTopicId = LogTopicId(34)
            )
        }
    }

    private fun TopicRepository.createFlowTopic(
        topicId: TopicId,
        flowId: FlowId,
        name: TopicName,
        logTopicId: LogTopicId = LogTopicId(3),
        groupId: GroupId = GroupId(4),
        cmdId: CmdId = CmdId(abs(Random(10).nextInt()) + 10)
    ) = create(
        TopicFlowCreateCmd(
            id = cmdId,
            topicId = topicId,
            logTopicId = logTopicId,
            groupId = groupId,
            flowId = flowId,
            name = name,
        )
    )

    private fun TopicRepository.createInternalTopic(
        topicId: TopicId,
        name: TopicName,
        logTopicId: LogTopicId = LogTopicId(3),
        cmdId: CmdId = CmdId(abs(Random(10).nextInt()) + 10)
    ) = create(
        TopicInternalCreateCmd(
            id = cmdId,
            topicId = topicId,
            logTopicId = logTopicId,
            name = name
        )
    )

    private fun TopicRepository.verifyCount(expected: Int) {
        verifyCount(expected) { }
    }

    private fun TopicRepository.verifyCount(expected: Int, block: TopicQuery.() -> Unit) {
        val counted = count(TopicQuery(groupIds = listOf()).also(block))
        assertThat("number of topic expected", counted, equalTo(Count(expected)))
    }
}