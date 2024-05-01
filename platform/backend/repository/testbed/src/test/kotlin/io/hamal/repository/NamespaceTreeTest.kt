package io.hamal.repository

import io.hamal.lib.common.TreeNode
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.CmdId.Companion.CmdId
import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceId.Companion.NamespaceId
import io.hamal.lib.domain.vo.NamespaceTreeId
import io.hamal.lib.domain.vo.NamespaceTreeId.Companion.NamespaceTreeId
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.domain.vo.WorkspaceId.Companion.WorkspaceId
import io.hamal.repository.api.NamespaceTreeCmdRepository.AppendCmd
import io.hamal.repository.api.NamespaceTreeCmdRepository.CreateCmd
import io.hamal.repository.api.NamespaceTreeQueryRepository.NamespaceTreeQuery
import io.hamal.repository.api.NamespaceTreeRepository
import io.hamal.repository.fixture.AbstractUnitTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import kotlin.math.abs
import kotlin.random.Random

internal class NamespaceTreeRepositoryTest : AbstractUnitTest() {

    @Nested
    inner class CreateNamespaceTreeTest {

        @TestFactory
        fun `Creates NamespaceTree`() = runWith(NamespaceTreeRepository::class) {
            val result = create(
                CreateCmd(
                    id = CmdId(1),
                    treeId = NamespaceTreeId(2),
                    workspaceId = WorkspaceId(3),
                    rootNodeId = NamespaceId(4)
                )
            )

            with(result) {
                assertThat(id, equalTo(NamespaceTreeId(2)))
                assertThat(workspaceId, equalTo(WorkspaceId(3)))
                assertThat(root, equalTo(TreeNode(NamespaceId(4))))
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Tries to create NamespaceTree but already exists in workspace`() =
            runWith(NamespaceTreeRepository::class) {
                create(
                    CreateCmd(
                        id = CmdId(1),
                        treeId = NamespaceTreeId(2),
                        workspaceId = WorkspaceId(3),
                        rootNodeId = NamespaceId(4)
                    )
                )

                assertThrows<IllegalArgumentException> {
                    create(
                        CreateCmd(
                            id = CmdId(2),
                            treeId = NamespaceTreeId(222),
                            workspaceId = WorkspaceId(3),
                            rootNodeId = NamespaceId(444)
                        )
                    )
                }.also { exception ->
                    assertThat(exception.message, equalTo("NamespaceTree already exists in workspace"))
                }

                verifyCount(1)
            }


        @TestFactory
        fun `Tries to create but cmd with namespace tree id was already applied`() =
            runWith(NamespaceTreeRepository::class) {
                createNamespaceTree(
                    cmdId = CmdId(1),
                    treeId = NamespaceTreeId(2),
                    workspaceId = WorkspaceId(3),
                    rootNodeId = NamespaceId(4)
                )

                val result = create(
                    CreateCmd(
                        id = CmdId(1),
                        treeId = NamespaceTreeId(2),
                        workspaceId = WorkspaceId(3333),
                        rootNodeId = NamespaceId(44444)
                    )
                )

                with(result) {
                    assertThat(id, equalTo(NamespaceTreeId(2)))
                    assertThat(workspaceId, equalTo(WorkspaceId(3)))
                    assertThat(root, equalTo(TreeNode(NamespaceId(4))))
                }

                verifyCount(1)
            }
    }

    @Nested
    inner class AppendNamespaceTest {
        @TestFactory
        fun `Appends namespace to tree`() = runWith(NamespaceTreeRepository::class) {
            createNamespaceTree(
                cmdId = CmdId(1),
                treeId = NamespaceTreeId(2),
                workspaceId = WorkspaceId(3),
                rootNodeId = NamespaceId(4)
            )

            append(
                AppendCmd(
                    id = CmdId(2),
                    treeId = NamespaceTreeId(2),
                    parentId = NamespaceId(4),
                    namespaceId = NamespaceId(5)
                )
            )

            with(get(NamespaceId(4))) {
                assertThat(id, equalTo(NamespaceTreeId(2)))
                assertThat(root.value, equalTo(NamespaceId(4)))
                assertThat(root.descendants, hasSize(1))
                assertThat(root.descendants[0].value, equalTo(NamespaceId(5)))
            }
            verifyCount(1)
        }

        @TestFactory
        fun `Tries to append same namespace twice`() = runWith(NamespaceTreeRepository::class) {
            createNamespaceTree(
                cmdId = CmdId(1),
                treeId = NamespaceTreeId(2),
                workspaceId = WorkspaceId(3),
                rootNodeId = NamespaceId(4)
            )

            append(
                AppendCmd(
                    id = CmdId(2),
                    treeId = NamespaceTreeId(2),
                    parentId = NamespaceId(4),
                    namespaceId = NamespaceId(5)
                )
            )

            assertThrows<IllegalArgumentException> {
                append(
                    AppendCmd(
                        id = CmdId(3),
                        treeId = NamespaceTreeId(2),
                        parentId = NamespaceId(4),
                        namespaceId = NamespaceId(5)
                    )
                )
            }.also { exception ->
                assertThat(exception.message, equalTo("Namespace already exists in NamespaceTree"))
            }

            with(get(NamespaceId(4))) {
                assertThat(id, equalTo(NamespaceTreeId(2)))
                assertThat(root.value, equalTo(NamespaceId(4)))
                assertThat(root.descendants, hasSize(1))
                assertThat(root.descendants[0].value, equalTo(NamespaceId(5)))
            }
            verifyCount(1)
        }
    }

    @Nested
    inner class ClearTest {

        @TestFactory
        fun `Nothing to clear`() = runWith(NamespaceTreeRepository::class) {
            clear()
            verifyCount(0)
        }


        @TestFactory
        fun `Clear table`() = runWith(NamespaceTreeRepository::class) {

            createNamespaceTree(
                cmdId = CmdId(1),
                treeId = NamespaceTreeId(2),
                workspaceId = WorkspaceId(3),
                rootNodeId = NamespaceId(4)
            )

            createNamespaceTree(
                cmdId = CmdId(1),
                treeId = NamespaceTreeId(22),
                workspaceId = WorkspaceId(33),
                rootNodeId = NamespaceId(44)
            )

            clear()
            verifyCount(0)
        }

    }


    @Nested
    inner class GetTest {

        @TestFactory
        fun `Get by root node id`() = runWith(NamespaceTreeRepository::class) {
            createNamespaceTree(
                cmdId = CmdId(1),
                treeId = NamespaceTreeId(2),
                workspaceId = WorkspaceId(3),
                rootNodeId = NamespaceId(4)
            )

            append(
                AppendCmd(
                    id = CmdId(2),
                    treeId = NamespaceTreeId(2),
                    parentId = NamespaceId(4),
                    namespaceId = NamespaceId(5)
                )
            )


            with(get(NamespaceId(4))) {
                assertThat(id, equalTo(NamespaceTreeId(2)))
                assertThat(workspaceId, equalTo(WorkspaceId(3)))
                assertThat(root, equalTo(TreeNode(NamespaceId(4), listOf(TreeNode(NamespaceId(5))))))
            }
        }

        @TestFactory
        fun `Get by node id`() = runWith(NamespaceTreeRepository::class) {
            createNamespaceTree(
                cmdId = CmdId(1),
                treeId = NamespaceTreeId(2),
                workspaceId = WorkspaceId(3),
                rootNodeId = NamespaceId(4)
            )

            append(
                AppendCmd(
                    id = CmdId(2),
                    treeId = NamespaceTreeId(2),
                    parentId = NamespaceId(4),
                    namespaceId = NamespaceId(5)
                )
            )


            with(get(NamespaceId(5))) {
                assertThat(id, equalTo(NamespaceTreeId(2)))
                assertThat(workspaceId, equalTo(WorkspaceId(3)))
                assertThat(root, equalTo(TreeNode(NamespaceId(4), listOf(TreeNode(NamespaceId(5))))))
            }
        }

        @TestFactory
        fun `Tries to get namespace by id but does not exist`() = runWith(NamespaceTreeRepository::class) {
            createNamespaceTree(
                cmdId = CmdId(1),
                treeId = NamespaceTreeId(2),
                workspaceId = WorkspaceId(3),
                rootNodeId = NamespaceId(4)
            )
            assertThrows<NoSuchElementException> {
                get(NamespaceId(111111))
            }.also { exception -> assertThat(exception.message, equalTo("Namespace not found")) }
        }
    }

    @Nested
    inner class FindTest {

        @TestFactory
        fun `Find by root node id`() = runWith(NamespaceTreeRepository::class) {
            createNamespaceTree(
                cmdId = CmdId(1),
                treeId = NamespaceTreeId(2),
                workspaceId = WorkspaceId(3),
                rootNodeId = NamespaceId(4)
            )

            append(
                AppendCmd(
                    id = CmdId(2),
                    treeId = NamespaceTreeId(2),
                    parentId = NamespaceId(4),
                    namespaceId = NamespaceId(5)
                )
            )


            with(find(NamespaceId(4))!!) {
                assertThat(id, equalTo(NamespaceTreeId(2)))
                assertThat(workspaceId, equalTo(WorkspaceId(3)))
                assertThat(root, equalTo(TreeNode(NamespaceId(4), listOf(TreeNode(NamespaceId(5))))))
            }
        }

        @TestFactory
        fun `Find by node id`() = runWith(NamespaceTreeRepository::class) {
            createNamespaceTree(
                cmdId = CmdId(1),
                treeId = NamespaceTreeId(2),
                workspaceId = WorkspaceId(3),
                rootNodeId = NamespaceId(4)
            )

            append(
                AppendCmd(
                    id = CmdId(2),
                    treeId = NamespaceTreeId(2),
                    parentId = NamespaceId(4),
                    namespaceId = NamespaceId(5)
                )
            )


            with(find(NamespaceId(5))!!) {
                assertThat(id, equalTo(NamespaceTreeId(2)))
                assertThat(workspaceId, equalTo(WorkspaceId(3)))
                assertThat(root, equalTo(TreeNode(NamespaceId(4), listOf(TreeNode(NamespaceId(5))))))
            }
        }

        @TestFactory
        fun `Tries to find namespace by id but does not exist`() = runWith(NamespaceTreeRepository::class) {
            createNamespaceTree(
                cmdId = CmdId(1),
                treeId = NamespaceTreeId(2),
                workspaceId = WorkspaceId(3),
                rootNodeId = NamespaceId(4)
            )
            val result = find(NamespaceId(111111))
            assertThat(result, nullValue())
        }
    }

    @Nested
    inner class ListAndCountTest {

        @TestFactory
        fun `By ids`() = runWith(NamespaceTreeRepository::class) {
            setup()

            val query = NamespaceTreeQuery(
                limit = Limit.all,
                treeIds = listOf(NamespaceTreeId(2), NamespaceTreeId(12))
            )

            val count = count(query)
            assertThat(count, equalTo(Count(2)))

            val result = list(query)
            assertThat(result, hasSize(2))
            assertThat(result[0].id, equalTo(NamespaceTreeId(12)))
            assertThat(result[1].id, equalTo(NamespaceTreeId(2)))
        }

        @TestFactory
        fun `By workspace ids`() = runWith(NamespaceTreeRepository::class) {
            setup()

            val query = NamespaceTreeQuery(
                limit = Limit.all,
                workspaceIds = listOf(WorkspaceId(3), WorkspaceId(13))
            )

            val count = count(query)
            assertThat(count, equalTo(Count(2)))

            val result = list(query)
            assertThat(result, hasSize(2))
            assertThat(result[0].id, equalTo(NamespaceTreeId(12)))
            assertThat(result[1].id, equalTo(NamespaceTreeId(2)))
        }

        @TestFactory
        fun `Limit`() = runWith(NamespaceTreeRepository::class) {
            setup()

            val query = NamespaceTreeQuery(
                limit = Limit(2),
                workspaceIds = listOf()
            )

            val count = count(query)
            assertThat(count, equalTo(Count(4)))

            val result = list(query)
            assertThat(result, hasSize(2))
            assertThat(result[0].id, equalTo(NamespaceTreeId(30)))
            assertThat(result[1].id, equalTo(NamespaceTreeId(20)))
        }

        @TestFactory
        fun `Skip and limit`() = runWith(NamespaceTreeRepository::class) {
            setup()

            val query = NamespaceTreeQuery(
                afterId = NamespaceTreeId(12),
                limit = Limit.one,
                workspaceIds = listOf()
            )

            val count = count(query)
            assertThat(count, equalTo(Count(1)))

            val result = list(query)
            assertThat(result, hasSize(1))
            assertThat(result[0].id, equalTo(NamespaceTreeId(2)))
        }


        private fun NamespaceTreeRepository.setup() {
            createNamespaceTree(
                cmdId = CmdId(1),
                treeId = NamespaceTreeId(2),
                workspaceId = WorkspaceId(3),
                rootNodeId = NamespaceId(4)
            )

            createNamespaceTree(
                cmdId = CmdId(2),
                treeId = NamespaceTreeId(12),
                workspaceId = WorkspaceId(13),
                rootNodeId = NamespaceId(14)
            )

            createNamespaceTree(
                cmdId = CmdId(3),
                treeId = NamespaceTreeId(20),
                workspaceId = WorkspaceId(23),
                rootNodeId = NamespaceId(24)
            )

            createNamespaceTree(
                cmdId = CmdId(4),
                treeId = NamespaceTreeId(30),
                workspaceId = WorkspaceId(33),
                rootNodeId = NamespaceId(34)
            )
        }
    }
}

private fun NamespaceTreeRepository.createNamespaceTree(
    treeId: NamespaceTreeId,
    workspaceId: WorkspaceId,
    rootNodeId: NamespaceId,
    cmdId: CmdId = CmdId(abs(Random(10).nextInt()) + 10)
) {

    create(
        CreateCmd(
            id = cmdId,
            treeId = treeId,
            workspaceId = workspaceId,
            rootNodeId = rootNodeId
        )
    )
}

private fun NamespaceTreeRepository.verifyCount(expected: Int) {
    verifyCount(expected) { }
}

private fun NamespaceTreeRepository.verifyCount(expected: Int, block: NamespaceTreeQuery.() -> Unit) {
    val counted = count(NamespaceTreeQuery(workspaceIds = listOf()).also(block))
    assertThat("number of namespaces expected", counted, equalTo(Count(expected)))
}