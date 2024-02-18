package io.hamal.repository

import io.hamal.lib.common.TreeNode
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceTreeId
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.repository.api.NamespaceTreeCmdRepository.CreateCmd
import io.hamal.repository.api.NamespaceTreeQueryRepository.NamespaceTreeQuery
import io.hamal.repository.api.NamespaceTreeRepository
import io.hamal.repository.fixture.AbstractUnitTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
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