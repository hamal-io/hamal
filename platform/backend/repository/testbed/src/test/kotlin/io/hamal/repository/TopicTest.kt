package io.hamal.repository

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain._enum.TopicType
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.TopicCmdRepository.TopicCreateCmd
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
    inner class CreateTopicWorkspace {

        @TestFactory
        fun `Creates topic`() = runWith(TopicRepository::class) {
            val result = create(
                TopicCreateCmd(
                    id = CmdId(1),
                    topicId = TopicId(2),
                    logTopicId = LogTopicId(3),
                    workspaceId = WorkspaceId(4),
                    namespaceId = NamespaceId(5),
                    name = TopicName("topic-name"),
                    type = TopicType.Workspace
                )
            )

            with(result) {
                assertThat(id, equalTo(TopicId(2)))
                assertThat(logTopicId, equalTo(LogTopicId(3)))
                assertThat(workspaceId, equalTo(WorkspaceId(4)))
                assertThat(namespaceId, equalTo(NamespaceId(5)))
                assertThat(name, equalTo(TopicName("topic-name")))
                assertThat(type, equalTo(TopicType.Workspace))
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Tries to create but same name already exists in namespace`() = runWith(TopicRepository::class) {
            createTopic(
                topicId = TopicId(1),
                workspaceId = WorkspaceId(3),
                namespaceId = NamespaceId(5),
                logTopicId = LogTopicId(4),
                name = TopicName("topic-name")
            )

            assertThrows<IllegalArgumentException> {
                create(
                    TopicCreateCmd(
                        id = CmdId(10),
                        topicId = TopicId(11),
                        logTopicId = LogTopicId(12),
                        workspaceId = WorkspaceId(3),
                        namespaceId = NamespaceId(5),
                        name = TopicName("topic-name"),
                        type = TopicType.Public
                    )
                )
            }.also { exception ->
                assertThat(
                    exception.message, equalTo("Topic already exists")
                )
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Creates with same name but different workspace`() = runWith(TopicRepository::class) {
            createTopic(
                topicId = TopicId(1),
                workspaceId = WorkspaceId(3),
                name = TopicName("first-topic-name")
            )

            create(
                TopicCreateCmd(
                    id = CmdId(10),
                    topicId = TopicId(11),
                    logTopicId = LogTopicId(12),
                    workspaceId = WorkspaceId(14),
                    namespaceId = NamespaceId(15),
                    name = TopicName("first-topic-name"),
                    type = TopicType.Public
                )
            )

            verifyCount(2)
        }

        @TestFactory
        fun `Tries to create but cmd with trigger id was already applied`() =
            runWith(TopicRepository::class) {

                createTopic(
                    cmdId = CmdId(23456),
                    topicId = TopicId(1),
                    workspaceId = WorkspaceId(3),
                    logTopicId = LogTopicId(4),
                    namespaceId = NamespaceId(5),
                    name = TopicName("first-topic"),
                    type = TopicType.Internal
                )

                val result = create(
                    TopicCreateCmd(
                        id = CmdId(23456),
                        topicId = TopicId(1),
                        workspaceId = WorkspaceId(333),
                        logTopicId = LogTopicId(4444),
                        namespaceId = NamespaceId(5555),
                        name = TopicName("second-topic"),
                        type = TopicType.Workspace
                    )
                )

                with(result) {
                    assertThat(id, equalTo(TopicId(1)))
                    assertThat(workspaceId, equalTo(WorkspaceId(3)))
                    assertThat(logTopicId, equalTo(LogTopicId(4)))
                    assertThat(namespaceId, equalTo(NamespaceId(5)))
                    assertThat(name, equalTo(TopicName("first-topic")))
                    assertThat(type, equalTo(TopicType.Internal))
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
            createTopic(
                cmdId = CmdId(1),
                topicId = TopicId(1),
                workspaceId = WorkspaceId(3),
                logTopicId = LogTopicId(4),
                name = TopicName("first-topic-name")
            )

            createTopic(
                cmdId = CmdId(2),
                topicId = TopicId(20),
                workspaceId = WorkspaceId(23),
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
            createTopic(
                topicId = TopicId(1),
                workspaceId = WorkspaceId(3),
                logTopicId = LogTopicId(4),
                name = TopicName("SomeTopic")
            )

            with(get(TopicId(1))) {
                assertThat(id, equalTo(TopicId(1)))
                assertThat(logTopicId, equalTo(LogTopicId(4)))
                assertThat(workspaceId, equalTo(WorkspaceId(3)))
                assertThat(name, equalTo(TopicName("SomeTopic")))
            }
        }

        @TestFactory
        fun `Tries to get topic by id but does not exist`() = runWith(TopicRepository::class) {
            createTopic(
                topicId = TopicId(1),
                workspaceId = WorkspaceId(3),
                logTopicId = LogTopicId(4),
                name = TopicName("SomeTopic")
            )

            assertThrows<NoSuchElementException> {
                get(TopicId(111111))
            }.also { exception ->
                assertThat(exception.message, equalTo("Topic not found"))
            }
        }


        @TestFactory
        fun `Get topic by namespace id and name`() = runWith(TopicRepository::class) {
            createTopic(
                topicId = TopicId(1),
                workspaceId = WorkspaceId(3),
                logTopicId = LogTopicId(4),
                namespaceId = NamespaceId(5),
                name = TopicName("SomeTopic")
            )

            with(getTopic(NamespaceId(5), TopicName("SomeTopic"))) {
                assertThat(id, equalTo(TopicId(1)))
                assertThat(logTopicId, equalTo(LogTopicId(4)))
                assertThat(workspaceId, equalTo(WorkspaceId(3)))
                assertThat(namespaceId, equalTo(NamespaceId(5)))
                assertThat(name, equalTo(TopicName("SomeTopic")))
            }
        }

        @TestFactory
        fun `Tries to get topic by namespace id and name but does not exist`() = runWith(TopicRepository::class) {
            createTopic(
                cmdId = CmdId(1),
                topicId = TopicId(1),
                workspaceId = WorkspaceId(3),
                logTopicId = LogTopicId(4),
                namespaceId = NamespaceId(5),
                name = TopicName("SomeTopic")
            )

            assertThrows<NoSuchElementException> {
                getTopic(NamespaceId(55555), TopicName("SomeTopic"))
            }.also { exception ->
                assertThat(exception.message, equalTo("Topic not found"))
            }

            assertThrows<NoSuchElementException> {
                getTopic(NamespaceId(5), TopicName("AnotherTopic"))
            }.also { exception ->
                assertThat(exception.message, equalTo("Topic not found"))
            }
        }
    }

    @Nested
    inner class FindTest {
        @TestFactory
        fun `Find topic by id`() = runWith(TopicRepository::class) {
            createTopic(
                topicId = TopicId(1),
                workspaceId = WorkspaceId(3),
                logTopicId = LogTopicId(4),
                name = TopicName("SomeTopic")
            )

            with(find(TopicId(1))!!) {

                assertThat(id, equalTo(TopicId(1)))
                assertThat(logTopicId, equalTo(LogTopicId(4)))
                assertThat(workspaceId, equalTo(WorkspaceId(3)))
                assertThat(name, equalTo(TopicName("SomeTopic")))
            }
        }

        @TestFactory
        fun `Tries to find topic by id but does not exist`() = runWith(TopicRepository::class) {
            createTopic(
                topicId = TopicId(1),
                workspaceId = WorkspaceId(3),
                logTopicId = LogTopicId(4),
                name = TopicName("SomeTopic")
            )
            val result = find(TopicId(111111))
            assertThat(result, nullValue())
        }

        @TestFactory
        fun `Finds topic by namespace id and name`() = runWith(TopicRepository::class) {
            createTopic(
                topicId = TopicId(1),
                workspaceId = WorkspaceId(3),
                logTopicId = LogTopicId(4),
                namespaceId = NamespaceId(5),
                name = TopicName("SomeTopic")
            )

            with(findTopic(NamespaceId(5), TopicName("SomeTopic"))!!) {
                assertThat(id, equalTo(TopicId(1)))
                assertThat(logTopicId, equalTo(LogTopicId(4)))
                assertThat(workspaceId, equalTo(WorkspaceId(3)))
                assertThat(name, equalTo(TopicName("SomeTopic")))
            }
        }

        @TestFactory
        fun `Tries to find topic by namespace id and name but does not exist`() = runWith(TopicRepository::class) {
            createTopic(
                cmdId = CmdId(1),
                topicId = TopicId(1),
                workspaceId = WorkspaceId(3),
                logTopicId = LogTopicId(4),
                namespaceId = NamespaceId(5),
                name = TopicName("SomeTopic")
            )

            assertThat(
                findTopic(NamespaceId(555555), TopicName("SomeTopic")),
                nullValue()
            )

            assertThat(
                findTopic(NamespaceId(5), TopicName("AnotherTopic")),
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
                assertThat(id, equalTo(TopicId(1)))
                assertThat(name, equalTo(TopicName("topic-one")))
                assertThat(workspaceId, equalTo(WorkspaceId(3)))
                assertThat(logTopicId, equalTo(LogTopicId(4)))
            }
        }

        @TestFactory
        fun `With workspace ids`() = runWith(TopicRepository::class) {
            setup()

            val query = TopicQuery(
                workspaceIds = listOf(WorkspaceId(3), WorkspaceId(13)),
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
        fun `With namespace ids`() = runWith(TopicRepository::class) {
            setup()

            val query = TopicQuery(
                namespaceIds = listOf(NamespaceId(5), NamespaceId(15)),
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
            createTopic(
                topicId = TopicId(1),
                workspaceId = WorkspaceId(3),
                logTopicId = LogTopicId(4),
                namespaceId = NamespaceId(5),
                name = TopicName("topic-one")
            )

            createTopic(
                topicId = TopicId(11),
                workspaceId = WorkspaceId(13),
                logTopicId = LogTopicId(14),
                namespaceId = NamespaceId(15),
                name = TopicName("topic-two")
            )

            createTopic(
                topicId = TopicId(21),
                workspaceId = WorkspaceId(23),
                logTopicId = LogTopicId(24),
                namespaceId = NamespaceId(25),
                name = TopicName("topic-three")
            )

            createTopic(
                topicId = TopicId(31),
                name = TopicName("topic-four"),
                logTopicId = LogTopicId(34),
                namespaceId = NamespaceId(35),
                type = TopicType.Internal
            )
        }
    }

    private fun TopicRepository.createTopic(
        topicId: TopicId,
        name: TopicName,
        logTopicId: LogTopicId = LogTopicId(3),
        workspaceId: WorkspaceId = WorkspaceId(4),
        namespaceId: NamespaceId = NamespaceId(5),
        type: TopicType = TopicType.Namespace,
        cmdId: CmdId = CmdId(abs(Random(10).nextInt()) + 10)
    ) = create(
        TopicCreateCmd(
            id = cmdId,
            topicId = topicId,
            logTopicId = logTopicId,
            workspaceId = workspaceId,
            namespaceId = namespaceId,
            name = name,
            type = type
        )
    )

    private fun TopicRepository.verifyCount(expected: Int) {
        verifyCount(expected) { }
    }

    private fun TopicRepository.verifyCount(expected: Int, block: TopicQuery.() -> Unit) {
        val counted = count(TopicQuery(workspaceIds = listOf()).also(block))
        assertThat("number of topic expected", counted, equalTo(Count(expected)))
    }
}