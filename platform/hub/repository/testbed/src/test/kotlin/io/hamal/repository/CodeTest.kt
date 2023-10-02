package io.hamal.repository

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.kua.type.CodeType
import io.hamal.repository.api.*
import io.hamal.repository.api.CodeCmdRepository.CreateCmd
import io.hamal.repository.api.CodeQueryRepository.CodeQuery
import io.hamal.repository.fixture.AbstractUnitTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import kotlin.io.path.createTempDirectory
import kotlin.math.abs
import kotlin.random.Random

internal class CodeRepositoryTest : AbstractUnitTest() {
    @Nested
    inner class CreateCodeTest {

        @TestFactory
        fun `Creates Func`() = runWith(CodeRepository::class) {
            val result = create(
                CreateCmd(
                    id = CmdId(1),
                    codeId = CodeId(123),
                    groupId = GroupId(1),
                    code = CodeValue("print(\"test\")")
                )
            )

            with(result) {
                assertThat(id, equalTo(CodeId(123)))
                assertThat(groupId, equalTo(GroupId(1)))
                assertThat(code, equalTo(CodeValue("print(\"test\")")))
            }
        }

        @TestFactory
        fun `code already exists`() = runWith(CodeRepository::class) {
            createCode(
                cmdId = CmdId(1234),
                codeId = CodeId(5),
                groupId = GroupId(6)
            )
        }

        val result = CodeCmdRepository.create(

            CreateCmd(
                id = CmdId(1234),
                codeId = CodeId(6),
                groupId = GroupId(6),
                code = codeValue("print(\"test\")")
            )
            )
        )
    }

    private fun CodeRepository.createCode(
        codeId: CodeId,
        groupId: GroupId,
        cmdId: CmdId = CmdId(abs(Random(10).nextInt()) + 10)
    ) {
        create(
            CreateCmd(
                id = cmdId,
                codeId = codeId,
                groupId = groupId,
                code = CodeValue("print(\"test\")")
            )
        )

    }

    private fun CodeRepository.verifyCount(expected: Int) {
        verifyCount(expected) { }
    }

    private fun CodeRepository.verifyCount(expected: Int, block: CodeQuery.() -> Unit) {
        val counted = count(CodeQuery(groupIds = listOf()).also(block))
        assertThat("number of code expected", counted, equalTo(expected.toULong()))
    }

    @Nested
    inner class UpdatesCodeTest {

    }

    @Nested
    inner class ClearTest {

    }

    @Nested
    inner class GetTest {

    }

    @Nested
    inner class FindTest {

    }

    @Nested
    inner class ListAndCountTest {

    }
}