package io.hamal.repository

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.CmdId.Companion.CmdId
import io.hamal.lib.common.domain.Count.Companion.Count
import io.hamal.lib.common.domain.Limit.Companion.Limit
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.CodeId.Companion.CodeId
import io.hamal.lib.domain.vo.CodeVersion.Companion.CodeVersion
import io.hamal.lib.domain.vo.ExtensionId.Companion.ExtensionId
import io.hamal.lib.domain.vo.ExtensionName.Companion.ExtensionName
import io.hamal.lib.domain.vo.WorkspaceId.Companion.WorkspaceId
import io.hamal.repository.api.ExtensionCmdRepository.CreateCmd
import io.hamal.repository.api.ExtensionCmdRepository.UpdateCmd
import io.hamal.repository.api.ExtensionCode
import io.hamal.repository.api.ExtensionQueryRepository.ExtensionQuery
import io.hamal.repository.api.ExtensionRepository
import io.hamal.repository.fixture.AbstractUnitTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import kotlin.math.abs
import kotlin.random.Random

internal class ExtensionRepositoryTest : AbstractUnitTest() {

    @Nested
    inner class CreateExtensionTest {
        @TestFactory
        fun `Creates Extension`() = runWith(ExtensionRepository::class) {
            val result = create(
                CreateCmd(
                    id = CmdId(1),
                    extensionId = ExtensionId(123),
                    workspaceId = WorkspaceId(1),
                    name = ExtensionName("Extension1"),
                    code = ExtensionCode(
                        id = CodeId(5),
                        version = CodeVersion(6)
                    )
                )
            )

            with(result) {
                assertThat(id, equalTo(ExtensionId(123)))
                assertThat(workspaceId, equalTo(WorkspaceId(1)))
                assertThat(name, equalTo(ExtensionName("Extension1")))
                assertThat(
                    code, equalTo(
                        ExtensionCode(
                            id = CodeId(5),
                            version = CodeVersion(6)
                        )
                    )
                )
            }
            verifyCount(1)
        }

        @TestFactory
        fun `Tries to create extension but name already exists in workspace`() =
            runWith(ExtensionRepository::class) {
                createExtension(
                    extensionId = ExtensionId(1),
                    workspaceId = WorkspaceId(1),
                    name = ExtensionName("TestExt")
                )

                val exception = assertThrows<IllegalArgumentException> {
                    create(
                        CreateCmd(
                            id = CmdId(2),
                            extensionId = ExtensionId(5),
                            workspaceId = WorkspaceId(1),
                            name = ExtensionName("TestExt"),
                            code = ExtensionCode(
                                id = CodeId(5),
                                version = CodeVersion(6)
                            )
                        )
                    )
                }

                assertThat(exception.message, equalTo("TestExt already exists in workspace 1"))
                verifyCount(1)
            }

        @TestFactory
        fun `Creates with same name but different workspace`() =
            runWith(ExtensionRepository::class) {
                createExtension(
                    extensionId = ExtensionId(1),
                    workspaceId = WorkspaceId(1),
                    name = ExtensionName("TestExt")
                )

                createExtension(
                    extensionId = ExtensionId(2),
                    workspaceId = WorkspaceId(2),
                    name = ExtensionName("TestExt")
                )

                verifyCount(2)
            }
    }


    @Nested
    inner class UpdatesExtensionTest {
        @TestFactory
        fun `Updates extension`() = runWith(ExtensionRepository::class) {
            createExtension(
                extensionId = ExtensionId(1),
                workspaceId = WorkspaceId(1),
                name = ExtensionName("TestExt")
            )

            val res = update(
                ExtensionId(1), UpdateCmd(
                    id = CmdId(2),
                    name = ExtensionName("UpdateExt"),
                    code = ExtensionCode(
                        id = CodeId(2),
                        version = CodeVersion(1)
                    )
                )
            )

            with(res) {
                assertThat(id, equalTo(ExtensionId(1)))
                assertThat(workspaceId, equalTo(WorkspaceId(1)))
                assertThat(name, equalTo(ExtensionName("UpdateExt")))
                assertThat(
                    code, equalTo(
                        ExtensionCode(
                            id = CodeId(2),
                            version = CodeVersion(1)
                        )
                    )
                )
            }
        }

        @Disabled
        @TestFactory
        fun `Tries to update but id does not exist`() = runWith(ExtensionRepository::class) {
            createExtension(
                extensionId = ExtensionId(1),
                workspaceId = WorkspaceId(1),
                name = ExtensionName("TestExt")
            )

            update(
                ExtensionId(999), UpdateCmd(
                    id = CmdId(2),
                    name = ExtensionName("UpdateExt"),
                    code = ExtensionCode(
                        id = CodeId(2),
                        version = CodeVersion(1)
                    )
                )
            )
        }
    }

    @Nested
    inner class ClearTest {
        @TestFactory
        fun `Nothing to clear`() = runWith(ExtensionRepository::class) {
            clear()
            verifyCount(0)
        }

        @TestFactory
        fun `Clear table`() = runWith(ExtensionRepository::class) {
            setup()
            clear()
            verifyCount(0)
        }
    }

    @Nested
    inner class GetTest {
        @TestFactory
        fun `Get extension by id`() = runWith(ExtensionRepository::class) {
            create(
                CreateCmd(
                    id = CmdId(1),
                    extensionId = ExtensionId(1),
                    workspaceId = WorkspaceId(1),
                    name = ExtensionName("TestExt"),
                    code = ExtensionCode(
                        id = CodeId(1),
                        version = CodeVersion(1)
                    )
                )
            )

            with(get(ExtensionId(1))) {
                assertThat(id, equalTo(ExtensionId(1)))
                assertThat(workspaceId, equalTo(WorkspaceId(1)))
                assertThat(name, equalTo(ExtensionName("TestExt")))
                assertThat(
                    code, equalTo(
                        ExtensionCode(
                            id = CodeId(1),
                            version = CodeVersion(1)
                        )
                    )
                )
            }
        }

        @TestFactory
        fun `Tries to get extension by id but does not exist`() = runWith(ExtensionRepository::class) {
            val exception = assertThrows<NoSuchElementException> {
                get(ExtensionId(111111))
            }
            assertThat(exception.message, equalTo("Extension not found"))
        }

    }

    @Nested
    inner class FindTest {
        @TestFactory
        fun `Find extension by id`() = runWith(ExtensionRepository::class) {
            create(
                CreateCmd(
                    id = CmdId(1),
                    extensionId = ExtensionId(1),
                    workspaceId = WorkspaceId(1),
                    name = ExtensionName("TestExt"),
                    code = ExtensionCode(
                        id = CodeId(1),
                        version = CodeVersion(1)
                    )
                )
            )

            with(find(ExtensionId(1))!!) {
                assertThat(id, equalTo(ExtensionId(1)))
                assertThat(workspaceId, equalTo(WorkspaceId(1)))
                assertThat(name, equalTo(ExtensionName("TestExt")))
                assertThat(
                    code, equalTo(
                        ExtensionCode(
                            id = CodeId(1),
                            version = CodeVersion(1)
                        )
                    )
                )
            }
        }

        @TestFactory
        fun `Tries to find extension by id but does not exist`() = runWith(ExtensionRepository::class) {
            assertThat(find(ExtensionId(111111)), nullValue())
        }
    }

    @Nested
    inner class ListAndCountTest {

        @TestFactory
        fun `By ids`() = runWith(ExtensionRepository::class) {
            setup()

            val result = list(listOf(ExtensionId(111111), ExtensionId(3)))
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(ExtensionId(3)))
                assertThat(workspaceId, equalTo(WorkspaceId(4)))
                assertThat(name, equalTo(ExtensionName("Extension3")))
            }

        }


        @TestFactory
        fun `With workspace ids`() = runWith(ExtensionRepository::class) {
            setup()

            val query = ExtensionQuery(
                workspaceIds = listOf(WorkspaceId(5), WorkspaceId(4)),
                limit = Limit(10)
            )

            assertThat(count(query), equalTo(Count(2)))
            val result = list(query)
            assertThat(result, hasSize(2))

            with(result[0]) {
                assertThat(id, equalTo(ExtensionId(4)))
                assertThat(workspaceId, equalTo(WorkspaceId(5)))
                assertThat(name, equalTo(ExtensionName("Extension4")))
            }

            with(result[1]) {
                assertThat(id, equalTo(ExtensionId(3)))
                assertThat(workspaceId, equalTo(WorkspaceId(4)))
                assertThat(name, equalTo(ExtensionName("Extension3")))
            }
        }


        @TestFactory
        fun `Limit`() = runWith(ExtensionRepository::class) {
            setup()

            val query = ExtensionQuery(
                workspaceIds = listOf(),
                limit = Limit(3)
            )

            assertThat(count(query), equalTo(Count(4)))
            val result = list(query)
            assertThat(result, hasSize(3))
        }

        @TestFactory
        fun `Skip and limit`() = runWith(ExtensionRepository::class) {
            setup()

            val query = ExtensionQuery(
                afterId = ExtensionId(2),
                workspaceIds = listOf(),
                limit = Limit(1)
            )

            assertThat(count(query), equalTo(Count(1)))
            val result = list(query)
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(ExtensionId(1)))
            }
        }
    }


}

private fun ExtensionRepository.createExtension(
    extensionId: ExtensionId,
    name: ExtensionName,
    workspaceId: WorkspaceId,
    codeId: CodeId = CodeId(5),
    codeVersion: CodeVersion = CodeVersion(6),
    cmdId: CmdId = CmdId(abs(Random(10).nextInt()) + 10)
) {
    create(
        CreateCmd(
            id = cmdId,
            extensionId = extensionId,
            workspaceId = workspaceId,
            name = name,
            code = ExtensionCode(
                id = codeId,
                version = codeVersion
            )
        )
    )
}

private fun ExtensionRepository.verifyCount(expected: Int) {
    verifyCount(expected) { }
}

private fun ExtensionRepository.verifyCount(expected: Int, block: ExtensionQuery.() -> Unit) {
    val counted = count(ExtensionQuery(workspaceIds = listOf()).also(block))
    assertThat("number of extensions expected", counted, equalTo(Count(expected)))
}

private fun ExtensionRepository.setup() {
    createExtension(
        extensionId = ExtensionId(1),
        workspaceId = WorkspaceId(3),
        name = ExtensionName("Extension1")
    )

    createExtension(
        extensionId = ExtensionId(2),
        workspaceId = WorkspaceId(3),
        name = ExtensionName("Extension2")
    )

    createExtension(
        extensionId = ExtensionId(3),
        workspaceId = WorkspaceId(4),
        name = ExtensionName("Extension3")
    )

    createExtension(
        extensionId = ExtensionId(4),
        workspaceId = WorkspaceId(5),
        name = ExtensionName("Extension4")
    )
}