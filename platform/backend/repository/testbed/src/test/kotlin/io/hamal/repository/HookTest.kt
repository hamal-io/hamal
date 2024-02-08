package io.hamal.repository

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.HookId
import io.hamal.lib.domain.vo.HookName
import io.hamal.repository.api.HookCmdRepository.CreateCmd
import io.hamal.repository.api.HookCmdRepository.UpdateCmd
import io.hamal.repository.api.HookQueryRepository.HookQuery
import io.hamal.repository.api.HookRepository
import io.hamal.repository.fixture.AbstractUnitTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import kotlin.math.abs
import kotlin.random.Random

internal class HookRepositoryTest : AbstractUnitTest() {

    @Nested
    inner class CreateHookTest {
        @TestFactory
        fun `Creates Hook`() = runWith(HookRepository::class) {
            val result = create(
                CreateCmd(
                    id = CmdId(1),
                    hookId = HookId(123),
                    groupId = GroupId(1),
                    flowId = FlowId(234),
                    name = HookName("SomeHook")
                )
            )

            with(result) {
                assertThat(id, equalTo(HookId(123)))
                assertThat(groupId, equalTo(GroupId(1)))
                assertThat(flowId, equalTo(FlowId(234)))
                assertThat(name, equalTo(HookName("SomeHook")))
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Tries to create but same name already exists in flow`() =
            runWith(HookRepository::class) {

                createHook(
                    hookId = HookId(1),
                    flowId = FlowId(2),
                    groupId = GroupId(3),
                    name = HookName("first-hook-name")
                )

                val exception = assertThrows<IllegalArgumentException> {
                    create(
                        CreateCmd(
                            id = CmdId(2),
                            hookId = HookId(4),
                            groupId = GroupId(3),
                            flowId = FlowId(2),
                            name = HookName("first-hook-name")
                        )
                    )
                }

                assertThat(
                    exception.message,
                    equalTo("HookName(first-hook-name) already exists in namespace FlowId(2)")
                )

                verifyCount(1)
            }

        @TestFactory
        fun `Creates with same name but different flow`() =
            runWith(HookRepository::class) {

                createHook(
                    hookId = HookId(1),
                    flowId = FlowId(2),
                    groupId = GroupId(3),
                    name = HookName("hook-name")
                )

                val result = create(
                    CreateCmd(
                        id = CmdId(2),
                        hookId = HookId(4),
                        groupId = GroupId(3),
                        flowId = FlowId(22),
                        name = HookName("hook-name")
                    )
                )

                with(result) {
                    assertThat(id, equalTo(HookId(4)))
                    assertThat(groupId, equalTo(GroupId(3)))
                    assertThat(flowId, equalTo(FlowId(22)))
                    assertThat(name, equalTo(HookName("hook-name")))
                }

                verifyCount(2)
            }

        @TestFactory
        fun `Tries to create but cmd with hook id was already applied`() =
            runWith(HookRepository::class) {

                createHook(
                    cmdId = CmdId(23456),
                    hookId = HookId(5),
                    flowId = FlowId(2),
                    groupId = GroupId(3),
                    name = HookName("first-hook-name")
                )


                val result = create(
                    CreateCmd(
                        id = CmdId(23456),
                        hookId = HookId(5),
                        groupId = GroupId(333),
                        flowId = FlowId(2222),
                        name = HookName("second-hook-name")
                    )
                )

                with(result) {
                    assertThat(id, equalTo(HookId(5)))
                    assertThat(groupId, equalTo(GroupId(3)))
                    assertThat(flowId, equalTo(FlowId(2)))
                    assertThat(name, equalTo(HookName("first-hook-name")))
                }

                verifyCount(1)
            }
    }

    @Nested
    inner class UpdatesHookTest {

        @TestFactory
        fun `Updates hook`() = runWith(HookRepository::class) {
            createHook(
                hookId = HookId(1),
                flowId = FlowId(2),
                groupId = GroupId(3),
                name = HookName("hook-name")
            )

            val result = update(
                HookId(1), UpdateCmd(
                    id = CmdId(2),
                    name = HookName("Updated")
                )
            )

            with(result) {
                assertThat(id, equalTo(HookId(1)))
                assertThat(groupId, equalTo(GroupId(3)))
                assertThat(flowId, equalTo(FlowId(2)))
                assertThat(name, equalTo(HookName("Updated")))
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Updates hook without updating it`() = runWith(HookRepository::class) {
            createHook(
                hookId = HookId(1),
                flowId = FlowId(2),
                groupId = GroupId(3),
                name = HookName("hook-name")
            )

            val result = update(
                HookId(1), UpdateCmd(
                    id = CmdId(2),
                    name = null
                )
            )

            with(result) {
                assertThat(id, equalTo(HookId(1)))
                assertThat(groupId, equalTo(GroupId(3)))
                assertThat(flowId, equalTo(FlowId(2)))
                assertThat(name, equalTo(HookName("hook-name")))
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Tries to update but same name already exists in flow`() =
            runWith(HookRepository::class) {

                createHook(
                    hookId = HookId(1),
                    flowId = FlowId(2),
                    groupId = GroupId(3),
                    name = HookName("already-exists")
                )

                createHook(
                    hookId = HookId(2),
                    flowId = FlowId(2),
                    groupId = GroupId(3),
                    name = HookName("to-update")
                )

                val exception = assertThrows<IllegalArgumentException> {
                    update(
                        HookId(2), UpdateCmd(
                            id = CmdId(2),
                            name = HookName("already-exists"),
                        )
                    )
                }

                assertThat(
                    exception.message,
                    equalTo("HookName(already-exists) already exists in namespace FlowId(2)")
                )

                with(get(HookId(2))) {
                    assertThat(id, equalTo(HookId(2)))
                    assertThat(flowId, equalTo(FlowId(2)))
                    assertThat(groupId, equalTo(GroupId(3)))
                    assertThat(name, equalTo(HookName("to-update")))
                }

                verifyCount(2)
            }

    }

    @Nested
    inner class ClearTest {

        @TestFactory
        fun `Nothing to clear`() = runWith(HookRepository::class) {
            clear()
            verifyCount(0)
        }

        @TestFactory
        fun `Clear table`() = runWith(HookRepository::class) {

            createHook(
                hookId = HookId(1),
                flowId = FlowId(2),
                groupId = GroupId(3),
                name = HookName("already-exists")
            )

            createHook(
                hookId = HookId(2),
                flowId = FlowId(2),
                groupId = GroupId(3),
                name = HookName("to-update")
            )

            clear()
            verifyCount(0)
        }

    }

    @Nested
    inner class GetTest {
        @TestFactory
        fun `Get hook by id`() = runWith(HookRepository::class) {
            createHook(
                hookId = HookId(1),
                flowId = FlowId(2),
                groupId = GroupId(3),
                name = HookName("SomeHook")
            )

            with(get(HookId(1))) {
                assertThat(id, equalTo(HookId(1)))
                assertThat(groupId, equalTo(GroupId(3)))
                assertThat(flowId, equalTo(FlowId(2)))
                assertThat(name, equalTo(HookName("SomeHook")))
            }
        }

        @TestFactory
        fun `Tries to get hook by id but does not exist`() = runWith(HookRepository::class) {
            createHook(
                hookId = HookId(1),
                flowId = FlowId(2),
                groupId = GroupId(3),
                name = HookName("SomeHook")
            )

            val exception = assertThrows<NoSuchElementException> {
                get(HookId(111111))
            }
            assertThat(exception.message, equalTo("Hook not found"))
        }
    }

    @Nested
    inner class FindTest {
        @TestFactory
        fun `Find hook by id`() = runWith(HookRepository::class) {
            createHook(
                hookId = HookId(1),
                flowId = FlowId(2),
                groupId = GroupId(3),
                name = HookName("SomeHook")
            )

            with(find(HookId(1))!!) {
                assertThat(id, equalTo(HookId(1)))
                assertThat(groupId, equalTo(GroupId(3)))
                assertThat(flowId, equalTo(FlowId(2)))
                assertThat(name, equalTo(HookName("SomeHook")))
            }
        }

        @TestFactory
        fun `Tries to find hook by id but does not exist`() = runWith(HookRepository::class) {
            createHook(
                hookId = HookId(1),
                flowId = FlowId(2),
                groupId = GroupId(3),
                name = HookName("SomeHook")
            )

            val result = find(HookId(111111))
            assertThat(result, nullValue())
        }
    }

    @Nested
    inner class ListAndCountTest {

        @TestFactory
        fun `By ids`() = runWith(HookRepository::class) {
            setup()

            val result = list(listOf(HookId(111111), HookId(3)))
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(HookId(3)))
                assertThat(flowId, equalTo(FlowId(4)))
                assertThat(groupId, equalTo(GroupId(4)))
                assertThat(name, equalTo(HookName("Hook")))
            }
        }

        @TestFactory
        fun `With group ids`() = runWith(HookRepository::class) {
            setup()

            val query = HookQuery(
                groupIds = listOf(GroupId(5), GroupId(4)),
                limit = Limit(10)
            )

            assertThat(count(query), equalTo(Count(2)))
            val result = list(query)
            assertThat(result, hasSize(2))

            with(result[0]) {
                assertThat(id, equalTo(HookId(4)))
                assertThat(flowId, equalTo(FlowId(10)))
                assertThat(groupId, equalTo(GroupId(5)))
                assertThat(name, equalTo(HookName("Hook")))
            }

            with(result[1]) {
                assertThat(id, equalTo(HookId(3)))
                assertThat(flowId, equalTo(FlowId(4)))
                assertThat(groupId, equalTo(GroupId(4)))
                assertThat(name, equalTo(HookName("Hook")))
            }
        }

        @TestFactory
        fun `With flow ids`() = runWith(HookRepository::class) {
            setup()

            val query = HookQuery(
                flowIds = listOf(FlowId(10), FlowId(23)),
                limit = Limit(10)
            )

            assertThat(count(query), equalTo(Count(1)))
            val result = list(query)
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(HookId(4)))
                assertThat(flowId, equalTo(FlowId(10)))
                assertThat(groupId, equalTo(GroupId(5)))
                assertThat(name, equalTo(HookName("Hook")))
            }
        }


        @TestFactory
        fun `Limit`() = runWith(HookRepository::class) {
            setup()

            val query = HookQuery(
                groupIds = listOf(),
                limit = Limit(3)
            )

            assertThat(count(query), equalTo(Count(4)))
            val result = list(query)
            assertThat(result, hasSize(3))
        }

        @TestFactory
        fun `Skip and limit`() = runWith(HookRepository::class) {
            setup()

            val query = HookQuery(
                afterId = HookId(2),
                groupIds = listOf(),
                limit = Limit(1)
            )

            assertThat(count(query), equalTo(Count(1)))
            val result = list(query)
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(HookId(1)))
            }
        }

        private fun HookRepository.setup() {
            createHook(
                hookId = HookId(1),
                flowId = FlowId(2),
                groupId = GroupId(3),
                name = HookName("Hook")
            )

            createHook(
                hookId = HookId(2),
                flowId = FlowId(3),
                groupId = GroupId(3),
                name = HookName("Hook")
            )

            createHook(
                hookId = HookId(3),
                flowId = FlowId(4),
                groupId = GroupId(4),
                name = HookName("Hook")
            )

            createHook(
                hookId = HookId(4),
                flowId = FlowId(10),
                groupId = GroupId(5),
                name = HookName("Hook")
            )
        }
    }
}

private fun HookRepository.createHook(
    hookId: HookId,
    flowId: FlowId,
    name: HookName,
    groupId: GroupId,
    cmdId: CmdId = CmdId(abs(Random(10).nextInt()) + 10)
) {
    create(
        CreateCmd(
            id = cmdId,
            hookId = hookId,
            groupId = groupId,
            flowId = flowId,
            name = name
        )
    )
}

private fun HookRepository.verifyCount(expected: Int) {
    verifyCount(expected) { }
}

private fun HookRepository.verifyCount(expected: Int, block: HookQuery.() -> Unit) {
    val counted = count(HookQuery(groupIds = listOf()).also(block))
    assertThat("number of hooktions expected", counted, equalTo(Count(expected)))
}