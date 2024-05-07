package io.hamal.repository

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.CmdId.Companion.CmdId
import io.hamal.lib.common.domain.Count.Companion.Count
import io.hamal.lib.common.domain.Limit.Companion.Limit
import io.hamal.lib.domain._enum.CodeTypes
import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.CodeId.Companion.CodeId
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.CodeValue.Companion.CodeValue
import io.hamal.lib.domain.vo.CodeVersion.Companion.CodeVersion
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.domain.vo.WorkspaceId.Companion.WorkspaceId
import io.hamal.repository.api.Code
import io.hamal.repository.api.CodeCmdRepository.CreateCmd
import io.hamal.repository.api.CodeCmdRepository.UpdateCmd
import io.hamal.repository.api.CodeQueryRepository.CodeQuery
import io.hamal.repository.api.CodeRepository
import io.hamal.repository.fixture.AbstractUnitTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import kotlin.math.abs
import kotlin.random.Random

internal class CodeRepositoryTest : AbstractUnitTest() {
    @Nested
    inner class CreateCodeTest {

        @TestFactory
        fun `Creates Code`() = runWith(CodeRepository::class) {
            val result = create(
                CreateCmd(
                    id = CmdId(1),
                    codeId = CodeId(123),
                    workspaceId = WorkspaceId(1),
                    value = CodeValue("40 + 2")
                )
            )

            with(result) {
                assertThat(id, equalTo(CodeId(123)))
                assertThat(workspaceId, equalTo(WorkspaceId(1)))
                assertThat(version, equalTo(CodeVersion(1)))
                assertThat(value, equalTo(CodeValue("40 + 2")))
                assertThat(type.enumValue, equalTo(CodeTypes.Lua54))
            }
        }

        @TestFactory
        fun `Creates code duplicate`() = runWith(CodeRepository::class) {
            createCode(
                codeId = CodeId(1),
                workspaceId = WorkspaceId(1),
                code = CodeValue("40 + 2")
            )

            createCode(
                codeId = CodeId(2),
                workspaceId = WorkspaceId(1),
                code = CodeValue("40 + 2")
            )

            verifyCount(2)

            with(get(CodeId(1))) {
                assertThat(id, equalTo(CodeId(1)))
                assertThat(version, equalTo(CodeVersion(1)))
                assertThat(value, equalTo(CodeValue("40 + 2")))
                assertThat(type.enumValue, equalTo(CodeTypes.Lua54))

            }

            with(get(CodeId(2))) {
                assertThat(id, equalTo(CodeId(2)))
                assertThat(version, equalTo(CodeVersion(1)))
                assertThat(value, equalTo(CodeValue("40 + 2")))
                assertThat(type.enumValue, equalTo(CodeTypes.Lua54))
            }
        }
    }

    @Nested
    inner class UpdatesCodeTest {

        @TestFactory
        fun `Updates code`() = runWith(CodeRepository::class) {
            createCode(
                codeId = CodeId(1),
                workspaceId = WorkspaceId(1),
                code = CodeValue("8 + 8")
            )

            val result = update(CodeId(1), UpdateCmd(CmdId(2), CodeValue("40 + 2")))

            with(result) {
                assertThat(id, equalTo(CodeId(1)))
                assertThat(workspaceId, equalTo(WorkspaceId(1)))
                assertThat(version, equalTo(CodeVersion(2)))
                assertThat(value, equalTo(CodeValue("40 + 2")))
                assertThat(type.enumValue, equalTo(CodeTypes.Lua54))
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Updates code multiple times`() = runWith(CodeRepository::class) {
            createCode(
                codeId = CodeId(1),
                workspaceId = WorkspaceId(1),
                code = CodeValue("8 + 8")
            )

            verifyCount(1)

            repeat(13) { iteration ->
                val result = update(CodeId(1), UpdateCmd(CmdId(iteration + 2), CodeValue("40 + $iteration")))

                with(result) {
                    assertThat(id, equalTo(CodeId(1)))
                    assertThat(workspaceId, equalTo(WorkspaceId(1)))
                    assertThat(version, equalTo(CodeVersion(iteration + 2)))
                    assertThat(value, equalTo(CodeValue("40 + $iteration")))
                    assertThat(type.enumValue, equalTo(CodeTypes.Lua54))

                }
            }
        }
    }

    @Nested
    inner class ClearTest {

        @TestFactory
        fun `Nothing to clear`() = runWith(CodeRepository::class) {
            clear()
            verifyCount(0)
        }

        @TestFactory
        fun `Clear table`() = runWith(CodeRepository::class) {

            createCode(
                codeId = CodeId(1),
                workspaceId = WorkspaceId(3),
                code = CodeValue("1 + 1")

            )

            createCode(
                codeId = CodeId(2),
                workspaceId = WorkspaceId(3),
                code = CodeValue("2 + 2")
            )

            clear()
            verifyCount(0)
        }
    }

    @Nested
    inner class GetTest {
        @TestFactory
        fun `Get code by id`() = runWith(CodeRepository::class) {
            createCode(
                codeId = CodeId(1),
                workspaceId = WorkspaceId(3),
                code = CodeValue("1 + 1")
            )

            with(get(CodeId(1))) {
                assertThat(id, equalTo(CodeId(1)))
                assertThat(workspaceId, equalTo(WorkspaceId(3)))
                assertThat(version, equalTo(CodeVersion(1)))
                assertThat(value, equalTo(CodeValue("1 + 1")))
                assertThat(type.enumValue, equalTo(CodeTypes.Lua54))

            }
        }

        @TestFactory
        fun `Get code by id and version`() = runWith(CodeRepository::class) {
            createCode(
                codeId = CodeId(1),
                workspaceId = WorkspaceId(3),
                code = CodeValue("created")
            )

            repeat(10) { iteration ->
                update(CodeId(1), UpdateCmd(CmdId(iteration + 2), CodeValue("1 + $iteration")))
            }

            with(get(CodeId(1), CodeVersion(1))) {
                assertThat(version, equalTo(CodeVersion(1)))
                assertThat(value, equalTo(CodeValue("created")))
            }

            with(get(CodeId(1), CodeVersion(2))) {
                assertThat(version, equalTo(CodeVersion(2)))
                assertThat(value, equalTo(CodeValue("1 + 0")))
            }

            with(get(CodeId(1), CodeVersion(5))) {
                assertThat(version, equalTo(CodeVersion(5)))
                assertThat(value, equalTo(CodeValue("1 + 3")))
                assertThat(type.enumValue, equalTo(CodeTypes.Lua54))
            }
        }

        @TestFactory
        fun `Tries to get code by id but does not exist`() = runWith(CodeRepository::class) {
            createCode(
                codeId = CodeId(1),
                workspaceId = WorkspaceId(3),
                code = CodeValue("1 + 1")
            )

            val exception = assertThrows<NoSuchElementException> {
                get(CodeId(111111))
            }
            assertThat(exception.message, equalTo("Code not found"))
        }

        @TestFactory
        fun `Tries to get code by id and version but version does not exists`() = runWith(CodeRepository::class) {
            createCode(
                codeId = CodeId(1),
                workspaceId = WorkspaceId(3),
                code = CodeValue("1 + 1")
            )

            assertThat(
                assertThrows<NoSuchElementException> { get(CodeId(1), CodeVersion(1000)) }.message,
                equalTo("Code not found")
            )
            assertThat(
                assertThrows<NoSuchElementException> { get(CodeId(1), CodeVersion(2)) }.message,
                equalTo("Code not found")
            )
        }

        @TestFactory
        fun `Tries to get code with negative version`() = runWith(CodeRepository::class) {
            createCode(
                codeId = CodeId(1),
                workspaceId = WorkspaceId(3),
                code = CodeValue("1 + 1")
            )
            assertThat(
                assertThrows<IllegalArgumentException> { get(CodeId(1), CodeVersion(-5)) }.message,
                equalTo("CodeVersion must be positive")
            )

            assertThat(
                assertThrows<IllegalArgumentException> { get(CodeId(1), CodeVersion(0)) }.message,
                equalTo("CodeVersion must be positive")
            )
        }
    }

    @Nested
    inner class FindTest {
        @TestFactory
        fun `Find code by id`() = runWith(CodeRepository::class) {
            createCode(
                codeId = CodeId(1),
                workspaceId = WorkspaceId(3),
                code = CodeValue("1 + 1")
            )

            with(find(CodeId(1))!!) {
                assertThat(id, equalTo(CodeId(1)))
                assertThat(workspaceId, equalTo(WorkspaceId(3)))
                assertThat(version, equalTo(CodeVersion(1)))
                assertThat(value, equalTo(CodeValue("1 + 1")))
                assertThat(type.enumValue, equalTo(CodeTypes.Lua54))
            }
        }

        @TestFactory
        fun `Find code by id and version`() = runWith(CodeRepository::class) {
            createCode(
                codeId = CodeId(1),
                workspaceId = WorkspaceId(3),
                code = CodeValue("created")
            )

            repeat(10) { iteration ->
                update(CodeId(1), UpdateCmd(CmdId(iteration + 2), CodeValue("1 + $iteration")))
            }

            with(find(CodeId(1), CodeVersion(1))!!) {
                assertThat(version, equalTo(CodeVersion(1)))
                assertThat(value, equalTo(CodeValue("created")))
                assertThat(type.enumValue, equalTo(CodeTypes.Lua54))
            }

            with(find(CodeId(1), CodeVersion(2))!!) {
                assertThat(version, equalTo(CodeVersion(2)))
                assertThat(value, equalTo(CodeValue("1 + 0")))
                assertThat(type.enumValue, equalTo(CodeTypes.Lua54))
            }

            with(find(CodeId(1), CodeVersion(5))!!) {
                assertThat(version, equalTo(CodeVersion(5)))
                assertThat(value, equalTo(CodeValue("1 + 3")))
                assertThat(type.enumValue, equalTo(CodeTypes.Lua54))
            }
        }

        @TestFactory
        fun `Tries to find code by id but does not exist`() = runWith(CodeRepository::class) {
            createCode(
                codeId = CodeId(1),
                workspaceId = WorkspaceId(3),
                code = CodeValue("1 + 1")
            )

            val result = find(CodeId(111111))
            assertThat(result, nullValue())
        }


        @TestFactory
        fun `Tries to find code by id and version but version does not exists`() = runWith(CodeRepository::class) {
            createCode(
                codeId = CodeId(1),
                workspaceId = WorkspaceId(3),
                code = CodeValue("1 + 1")
            )

            assertThat(find(CodeId(1), CodeVersion(1000)), nullValue())
            assertThat(find(CodeId(1), CodeVersion(2)), nullValue())
        }

        @TestFactory
        fun `Tries to get code with negative version`() = runWith(CodeRepository::class) {
            createCode(
                codeId = CodeId(1),
                workspaceId = WorkspaceId(3),
                code = CodeValue("1 + 1")
            )
            assertThat(
                assertThrows<IllegalArgumentException> { get(CodeId(1), CodeVersion(-5)) }.message,
                equalTo("CodeVersion must be positive")
            )

            assertThat(
                assertThrows<IllegalArgumentException> { get(CodeId(1), CodeVersion(0)) }.message,
                equalTo("CodeVersion must be positive")
            )
        }
    }

    @Nested
    inner class ListAndCountTest {
        @TestFactory
        fun `By ids`() = runWith(CodeRepository::class) {
            setup()

            val result = list(listOf(CodeId(111111), CodeId(3)))
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(CodeId(3)))
                assertThat(workspaceId, equalTo(WorkspaceId(4)))
                assertThat(value, equalTo(CodeValue("1 + 3")))
                assertThat(type.enumValue, equalTo(CodeTypes.Lua54))
            }
        }

        @TestFactory
        fun `With workspace ids`() = runWith(CodeRepository::class) {
            setup()

            val query = CodeQuery(
                workspaceIds = listOf(WorkspaceId(5), WorkspaceId(4)),
                limit = Limit(10)
            )

            assertThat(count(query), equalTo(Count(2)))
            val result = list(query)
            assertThat(result, hasSize(2))

            with(result[0]) {
                assertThat(id, equalTo(CodeId(4)))
                assertThat(workspaceId, equalTo(WorkspaceId(5)))
                assertThat(value, equalTo(CodeValue("1 + 4")))
                assertThat(type.enumValue, equalTo(CodeTypes.Lua54))
            }

            with(result[1]) {
                assertThat(id, equalTo(CodeId(3)))
                assertThat(workspaceId, equalTo(WorkspaceId(4)))
                assertThat(value, equalTo(CodeValue("1 + 3")))
                assertThat(type.enumValue, equalTo(CodeTypes.Lua54))
            }
        }


        private fun CodeRepository.setup() {
            createCode(
                codeId = CodeId(1),
                workspaceId = WorkspaceId(3),
                code = CodeValue("1 + 1")
            )
            createCode(
                codeId = CodeId(2),
                workspaceId = WorkspaceId(3),
                code = CodeValue("1 + 2")
            )
            createCode(
                codeId = CodeId(3),
                workspaceId = WorkspaceId(4),
                code = CodeValue("1 + 3")
            )
            createCode(
                codeId = CodeId(4),
                workspaceId = WorkspaceId(5),
                code = CodeValue("1 + 4")
            )
        }

        @TestFactory
        fun `Limit`() = runWith(CodeRepository::class) {
            setup()

            val query = CodeQuery(
                workspaceIds = listOf(),
                limit = Limit(3)
            )

            assertThat(count(query), equalTo(Count(4)))
            val result = list(query)
            assertThat(result, hasSize(3))
        }

        @TestFactory
        fun `Skip and limit`() = runWith(CodeRepository::class) {
            setup()

            val query = CodeQuery(
                afterId = CodeId(2),
                workspaceIds = listOf(),
                limit = Limit(1)
            )

            assertThat(count(query), equalTo(Count(1)))
            val result = list(query)
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(CodeId(1)))
            }
        }

    }
}

private fun CodeRepository.createCode(
    codeId: CodeId,
    workspaceId: WorkspaceId,
    cmdId: CmdId = CmdId(abs(Random(10).nextInt()) + 10),
    code: CodeValue
): Code {
    return create(
        CreateCmd(
            id = cmdId,
            codeId = codeId,
            workspaceId = workspaceId,
            value = code
        )
    )
}

private fun CodeRepository.verifyCount(expected: Int) {
    verifyCount(expected) { }
}

private fun CodeRepository.verifyCount(expected: Int, block: CodeQuery.() -> Unit) {
    val counted = count(CodeQuery(workspaceIds = listOf()).also(block))
    assertThat("number of code expected", counted, equalTo(Count(expected)))
}


