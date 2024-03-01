package io.hamal.repository

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.domain.vo.WorkspaceName
import io.hamal.repository.api.WorkspaceCmdRepository.CreateCmd
import io.hamal.repository.api.WorkspaceQueryRepository.WorkspaceQuery
import io.hamal.repository.api.WorkspaceRepository
import io.hamal.repository.fixture.AbstractUnitTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import kotlin.math.abs
import kotlin.random.Random

internal class WorkspaceRepositoryTest : AbstractUnitTest() {

    @Nested
    inner class CreateTest {
        @TestFactory
        fun `Creates Workspace`() = runWith(WorkspaceRepository::class) {
            val result = create(
                CreateCmd(
                    id = CmdId(1),
                    workspaceId = WorkspaceId(123),
                    name = WorkspaceName("SomeWorkspace"),
                    creatorId = AccountId(234)
                )
            )

            with(result) {
                assertThat(id, equalTo(WorkspaceId(123)))
                assertThat(name, equalTo(WorkspaceName("SomeWorkspace")))
                assertThat(creatorId, equalTo(AccountId(234)))
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Tries to create but same name already exists`() =
            runWith(WorkspaceRepository::class) {

                createWorkspace(
                    cmdId = CmdId(1),
                    workspaceId = WorkspaceId(123),
                    name = WorkspaceName("SomeWorkspace"),
                    creatorId = AccountId(234)
                )

                val exception = assertThrows<IllegalArgumentException> {
                    create(
                        CreateCmd(
                            id = CmdId(2),
                            workspaceId = WorkspaceId(321),
                            creatorId = AccountId(234),
                            name = WorkspaceName("SomeWorkspace")
                        )
                    )
                }

                assertThat(
                    exception.message,
                    equalTo("WorkspaceName(SomeWorkspace) already exists")
                )

                verifyCount(1)
            }

        @TestFactory
        fun `Tries to create but cmd with workspace id was already applied`() =
            runWith(WorkspaceRepository::class) {

                createWorkspace(
                    cmdId = CmdId(23456),
                    workspaceId = WorkspaceId(2),
                    name = WorkspaceName("SomeWorkspace"),
                    creatorId = AccountId(3)
                )

                val result = create(
                    CreateCmd(
                        id = CmdId(23456),
                        workspaceId = WorkspaceId(2),
                        creatorId = AccountId(234),
                        name = WorkspaceName("AnotherWorkspaceName")
                    )
                )

                with(result) {
                    assertThat(id, equalTo(WorkspaceId(2)))
                    assertThat(name, equalTo(WorkspaceName("SomeWorkspace")))
                    assertThat(creatorId, equalTo(AccountId(3)))
                }

                verifyCount(1)
            }
    }

    @Nested
    inner class ClearTest {

        @TestFactory
        fun `Nothing to clear`() = runWith(WorkspaceRepository::class) {
            clear()
            verifyCount(0)
        }

        @TestFactory
        fun `Clear table`() = runWith(WorkspaceRepository::class) {
            createWorkspace(
                name = WorkspaceName("already-exists"),
                workspaceId = WorkspaceId(1),
                creatorId = AccountId(2)
            )

            createWorkspace(
                name = WorkspaceName("to-update"),
                workspaceId = WorkspaceId(3),
                creatorId = AccountId(4)
            )

            clear()
            verifyCount(0)
        }

    }

    @Nested
    inner class GetTest {
        @TestFactory
        fun `Get workspace by id`() = runWith(WorkspaceRepository::class) {
            createWorkspace(
                workspaceId = WorkspaceId(1),
                creatorId = AccountId(3),
                name = WorkspaceName("SomeWorkspace")
            )

            with(get(WorkspaceId(1))) {
                assertThat(id, equalTo(WorkspaceId(1)))
                assertThat(creatorId, equalTo(AccountId(3)))
                assertThat(name, equalTo(WorkspaceName("SomeWorkspace")))
            }
        }

        @TestFactory
        fun `Tries to get workspace by id but does not exist`() = runWith(WorkspaceRepository::class) {
            createWorkspace(
                workspaceId = WorkspaceId(1),
                creatorId = AccountId(3),
                name = WorkspaceName("SomeWorkspace")
            )

            val exception = assertThrows<NoSuchElementException> {
                get(WorkspaceId(111111))
            }
            assertThat(exception.message, equalTo("Workspace not found"))
        }
    }

    @Nested
    inner class FindTest {
        @TestFactory
        fun `Find workspace by id`() = runWith(WorkspaceRepository::class) {
            createWorkspace(
                workspaceId = WorkspaceId(1),
                creatorId = AccountId(3),
                name = WorkspaceName("SomeWorkspace")
            )

            with(find(WorkspaceId(1))!!) {
                assertThat(id, equalTo(WorkspaceId(1)))
                assertThat(creatorId, equalTo(AccountId(3)))
                assertThat(name, equalTo(WorkspaceName("SomeWorkspace")))
            }
        }

        @TestFactory
        fun `Tries to find workspace by id but does not exist`() = runWith(WorkspaceRepository::class) {
            createWorkspace(
                workspaceId = WorkspaceId(1),
                creatorId = AccountId(3),
                name = WorkspaceName("SomeWorkspace")
            )

            val result = find(WorkspaceId(111111))
            assertThat(result, nullValue())
        }
    }

    @Nested
    inner class ListAndCountTest {

        @TestFactory
        fun `By account ids`() = runWith(WorkspaceRepository::class) {
            setup()

            val query = WorkspaceQuery(
                limit = Limit.all,
                accountIds = listOf(
                    AccountId(4),
                    AccountId(5)
                )
            )

            assertThat(count(query), equalTo(Count(2)))

            val result = list(query)
            assertThat(result, hasSize(2))
            assertThat(result[0].id, equalTo(WorkspaceId(4)))
            assertThat(result[1].id, equalTo(WorkspaceId(3)))
        }


        @TestFactory
        fun `By ids`() = runWith(WorkspaceRepository::class) {
            setup()

            val result = list(listOf(WorkspaceId(111111), WorkspaceId(3)))
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(WorkspaceId(3)))
                assertThat(creatorId, equalTo(AccountId(4)))
                assertThat(name, equalTo(WorkspaceName("Workspace-Three")))
            }
        }

        @TestFactory
        fun `With workspace ids`() = runWith(WorkspaceRepository::class) {
            setup()

            val query = WorkspaceQuery(
                workspaceIds = listOf(WorkspaceId(4), WorkspaceId(3)),
                limit = Limit(10)
            )

            assertThat(count(query), equalTo(Count(2)))
            val result = list(query)
            assertThat(result, hasSize(2))

            with(result[0]) {
                assertThat(id, equalTo(WorkspaceId(4)))
                assertThat(creatorId, equalTo(AccountId(5)))
                assertThat(name, equalTo(WorkspaceName("Workspace-Four")))
            }

            with(result[1]) {
                assertThat(id, equalTo(WorkspaceId(3)))
                assertThat(creatorId, equalTo(AccountId(4)))
                assertThat(name, equalTo(WorkspaceName("Workspace-Three")))
            }
        }


        @TestFactory
        fun `Limit`() = runWith(WorkspaceRepository::class) {
            setup()

            val query = WorkspaceQuery(
                workspaceIds = listOf(),
                limit = Limit(3)
            )

            assertThat(count(query), equalTo(Count(4)))
            val result = list(query)
            assertThat(result, hasSize(3))
        }

        @TestFactory
        fun `Skip and limit`() = runWith(WorkspaceRepository::class) {
            setup()

            val query = WorkspaceQuery(
                afterId = WorkspaceId(2),
                workspaceIds = listOf(),
                limit = Limit(1)
            )

            assertThat(count(query), equalTo(Count(1)))
            val result = list(query)
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(WorkspaceId(1)))
            }
        }

        private fun WorkspaceRepository.setup() {
            createWorkspace(
                workspaceId = WorkspaceId(1),
                creatorId = AccountId(3),
                name = WorkspaceName("Workspace-One")
            )

            createWorkspace(
                workspaceId = WorkspaceId(2),
                creatorId = AccountId(3),
                name = WorkspaceName("Workspace-Two")
            )

            createWorkspace(
                workspaceId = WorkspaceId(3),
                creatorId = AccountId(4),
                name = WorkspaceName("Workspace-Three")
            )

            createWorkspace(
                workspaceId = WorkspaceId(4),
                creatorId = AccountId(5),
                name = WorkspaceName("Workspace-Four")
            )
        }
    }
}

private fun WorkspaceRepository.createWorkspace(
    name: WorkspaceName,
    workspaceId: WorkspaceId,
    creatorId: AccountId,
    cmdId: CmdId = CmdId(abs(Random(10).nextInt()) + 10)
) {
    create(
        CreateCmd(
            id = cmdId,
            workspaceId = workspaceId,
            name = name,
            creatorId = creatorId
        )
    )
}

private fun WorkspaceRepository.verifyCount(expected: Int) {
    verifyCount(expected) { }
}

private fun WorkspaceRepository.verifyCount(expected: Int, block: WorkspaceQuery.() -> Unit) {
    val counted = count(WorkspaceQuery(workspaceIds = listOf()).also(block))
    assertThat("number of workspaces expected", counted, equalTo(Count(expected)))
}