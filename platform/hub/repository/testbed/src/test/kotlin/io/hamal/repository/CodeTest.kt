package io.hamal.repository

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.GroupId
import io.hamal.repository.api.CodeCmdRepository
import io.hamal.repository.api.CodeCmdRepository.CreateCmd
import io.hamal.repository.api.CodeId
import io.hamal.repository.api.CodeQueryRepository.CodeQuery
import io.hamal.repository.api.CodeRepository
import io.hamal.repository.api.CodeValue
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
                    groupId = GroupId(1),
                    code = CodeValue("40 + 2")
                )
            )

            with(result) {
                assertThat(id, equalTo(CodeId(123)))
                assertThat(groupId, equalTo(GroupId(1)))
                assertThat(code, equalTo(CodeValue("40 + 2")))
            }
        }

        @TestFactory
        fun `Creates code duplicate`() = runWith(CodeRepository::class) {
            runWith(CodeRepository::class) {

                createCode(
                    codeId = CodeId(1),
                    groupId = GroupId(1),
                    codeValue = CodeValue("40 + 2")
                )

                createCode(
                    codeId = CodeId(2),
                    groupId = GroupId(1),
                    codeValue = CodeValue("40 + 2")
                )

                assertThat(get(CodeId(1)).code, equalTo(CodeValue("40 + 2")))
                assertThat(get(CodeId(2)).code, equalTo(CodeValue("40 + 2")))
                verifyCount(2)
            }
        }
    }

    @Nested
    inner class UpdatesCodeTest {

        @TestFactory
        fun `Updates code`() = runWith(CodeRepository::class) {
            createCode(
                codeId = CodeId(1),
                groupId = GroupId(1),
                codeValue = CodeValue("8 + 8")
            )

            val result = update(
                CodeId(1), CodeCmdRepository.UpdateCmd(
                    id = CmdId(2),
                    code = CodeValue("40 + 2")
                )
            )

            with(result) {
                assertThat(id, equalTo(CodeId(1)))
                assertThat(groupId, equalTo(GroupId(1)))
                assertThat(code, equalTo(CodeValue("40 + 2")))
            }

            verifyCount(1)
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
                groupId = GroupId(3),
                codeValue = CodeValue("1 + 1")

            )

            createCode(
                codeId = CodeId(2),
                groupId = GroupId(3),
                codeValue = CodeValue("2 + 2")
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
                groupId = GroupId(3),
                codeValue = CodeValue("1 + 1")
            )

            with(get(CodeId(1))) {
                assertThat(id, equalTo(CodeId(1)))
                assertThat(groupId, equalTo(GroupId(3)))
                assertThat(code, equalTo(CodeValue("1 + 1")))
            }
        }

        @TestFactory
        fun `Tries to get code by id but does not exist`() = runWith(CodeRepository::class) {
            createCode(
                codeId = CodeId(1),
                groupId = GroupId(3),
                codeValue = CodeValue("1 + 1")
            )

            val exception = assertThrows<NoSuchElementException> {
                get(CodeId(111111))
            }
            assertThat(exception.message, equalTo("Code not found"))
        }
    }

    @Nested
    inner class FindTest {
        @TestFactory
        fun `Find code by id`() = runWith(CodeRepository::class) {
            createCode(
                codeId = CodeId(1),
                groupId = GroupId(3),
                codeValue = CodeValue("1 + 1")
            )

            with(find(CodeId(1))!!) {
                assertThat(id, equalTo(CodeId(1)))
                assertThat(groupId, equalTo(GroupId(3)))
                assertThat(code, equalTo(CodeValue("1 + 1")))
            }
        }

        @TestFactory
        fun `Tries to find func by id but does not exist`() = runWith(CodeRepository::class) {
            createCode(
                codeId = CodeId(1),
                groupId = GroupId(3),
                codeValue = CodeValue("1 + 1")
            )

            val result = find(CodeId(111111))
            assertThat(result, nullValue())
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
                assertThat(groupId, equalTo(GroupId(4)))
                assertThat(code, equalTo(CodeValue("1 + 3")))
            }
        }

        @TestFactory
        fun `With group ids`() = runWith(CodeRepository::class) {
            setup()

            val query = CodeQuery(
                groupIds = listOf(GroupId(5), GroupId(4)),
                limit = Limit(10)
            )

            assertThat(count(query), equalTo(2UL))
            val result = list(query)
            assertThat(result, hasSize(2))

            with(result[0]) {
                assertThat(id, equalTo(CodeId(4)))
                assertThat(groupId, equalTo(GroupId(5)))
                assertThat(code, equalTo(CodeValue("1 + 4")))
            }

            with(result[1]) {
                assertThat(id, equalTo(CodeId(3)))
                assertThat(groupId, equalTo(GroupId(4)))
                assertThat(code, equalTo(CodeValue("1 + 3")))
            }
        }


        private fun CodeRepository.setup() {
            createCode(
                codeId = CodeId(1),
                groupId = GroupId(3),
                codeValue = CodeValue("1 + 1")
            )
            createCode(
                codeId = CodeId(2),
                groupId = GroupId(3),
                codeValue = CodeValue("1 + 2")
            )
            createCode(
                codeId = CodeId(3),
                groupId = GroupId(4),
                codeValue = CodeValue("1 + 3")
            )
            createCode(
                codeId = CodeId(4),
                groupId = GroupId(5),
                codeValue = CodeValue("1 + 4")
            )
        }

        @TestFactory
        fun `Limit`() = runWith(CodeRepository::class) {
            setup()

            val query = CodeQuery(
                groupIds = listOf(),
                limit = Limit(3)
            )

            assertThat(count(query), equalTo(4UL))
            val result = list(query)
            assertThat(result, hasSize(3))
        }

        @TestFactory
        fun `Skip and limit`() = runWith(CodeRepository::class) {
            setup()

            val query = CodeQuery(
                afterId = CodeId(2),
                groupIds = listOf(),
                limit = Limit(1)
            )

            assertThat(count(query), equalTo(1UL))
            val result = list(query)
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(CodeId(1)))
            }
        }

    }

    private fun CodeRepository.createCode(
        codeId: CodeId,
        groupId: GroupId,
        cmdId: CmdId = CmdId(abs(Random(10).nextInt()) + 10),
        codeValue: CodeValue
    ) {
        create(
            CreateCmd(
                id = cmdId,
                codeId = codeId,
                groupId = groupId,
                code = codeValue
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

}