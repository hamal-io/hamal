package io.hamal.repository

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.repository.api.SnippetCmdRepository.*
import io.hamal.repository.api.SnippetQueryRepository.*
import io.hamal.repository.api.SnippetRepository
import io.hamal.repository.fixture.AbstractUnitTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory
import kotlin.math.abs
import kotlin.random.Random

class SnippetRepositoryTest : AbstractUnitTest() {
    @TestFactory
    fun `Creates Snippet`() = runWith(SnippetRepository::class) {
        val result = create(
            CreateCmd(
                id = CmdId(1),
                snippetId = SnippetId(123),
                groupId = GroupId(1),
                name = SnippetName("TestSnippet"),
                inputs = SnippetInputs(
                    MapType(
                        mutableMapOf(
                            "hamal" to StringType("rockz")
                        )
                    )
                ),
                codeValue = CodeValue("1 + 1"),
                accountId = AccountId("123")
            )
        )

        with(result) {
            assertThat(id, equalTo(SnippetId(123)))
            assertThat(groupId, equalTo(GroupId(1)))
            assertThat(name, equalTo(SnippetName("TestSnippet")))
            assertThat(inputs, equalTo(SnippetInputs(MapType(mutableMapOf("hamal" to StringType("rockz"))))))
            assertThat(codeValue, equalTo(CodeValue("1 + 1")))
            assertThat(accountId, equalTo(AccountId("123")))
        }
        verifyCount(1)
    }

    @TestFactory
    private fun SnippetRepository.createFunc(
        snippetId: SnippetId,
        name: SnippetName,
        groupId: GroupId,
        cmdId: CmdId = CmdId(abs(Random(10).nextInt()) + 10)
    ) {
        create(
            CreateCmd(
                id = cmdId,
                snippetId = snippetId,
                groupId = groupId,
                name = name,
                inputs = SnippetInputs(
                    MapType(
                        mutableMapOf(
                            "hamal" to StringType("rockz")
                        )
                    )
                ),
                codeValue = CodeValue("1 + 1"),
                accountId = AccountId("123")
            )
        )
    }

    private fun SnippetRepository.verifyCount(expected: Int) {
        verifyCount(expected) {}
    }

    private fun SnippetRepository.verifyCount(expected: Int, block: SnippetQuery.() -> Unit) {
        val counted = count(SnippetQuery(groupIds = listOf()).also(block))
        assertThat("number of snippets expected", counted, equalTo(expected.toULong()))
    }
}