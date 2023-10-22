package io.hamal.repository

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.NumberType
import io.hamal.lib.kua.type.StringType
import io.hamal.repository.api.SnippetCmdRepository.CreateCmd
import io.hamal.repository.api.SnippetCmdRepository.UpdateCmd
import io.hamal.repository.api.SnippetQueryRepository.SnippetQuery
import io.hamal.repository.api.SnippetRepository
import io.hamal.repository.fixture.AbstractUnitTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import kotlin.math.abs
import kotlin.random.Random

class SnippetRepositoryTest : AbstractUnitTest() {

    @Nested
    inner class CreatesSnippetTest {

        @TestFactory
        fun `Creates Snippet`() = runWith(SnippetRepository::class) {
            val result = create(
                CreateCmd(
                    id = CmdId(1),
                    snippetId = SnippetId(123),
                    groupId = GroupId(1),
                    creatorId = AccountId("123"),
                    name = SnippetName("TestSnippet"),
                    inputs = SnippetInputs(
                        MapType(
                            mutableMapOf(
                                "hamal" to StringType("rockz")
                            )
                        )
                    ),
                    value = CodeValue("1 + 1")
                )
            )

            with(result) {
                assertThat(id, equalTo(SnippetId(123)))
                assertThat(groupId, equalTo(GroupId(1)))
                assertThat(creatorId, equalTo(AccountId("123")))
                assertThat(name, equalTo(SnippetName("TestSnippet")))
                assertThat(inputs, equalTo(SnippetInputs(MapType(mutableMapOf("hamal" to StringType("rockz"))))))
                assertThat(value, equalTo(CodeValue("1 + 1")))
            }
            verifyCount(1)
        }

        @TestFactory
        fun `Creates snippet duplicate`() = runWith(SnippetRepository::class) {
            createSnippet(
                snippetId = SnippetId(1),
                groupId = GroupId(1),
                name = SnippetName("TestSnippet"),
                value = CodeValue("40 + 2")
            )

            createSnippet(
                snippetId = SnippetId(2),
                groupId = GroupId(1),
                name = SnippetName("TestSnippet"),
                value = CodeValue("40 + 2")
            )

            verifyCount(2)

            with(get(SnippetId(1))) {
                assertThat(id, equalTo(SnippetId(1)))
                assertThat(value, equalTo(CodeValue("40 + 2")))
                assertThat(name, equalTo(SnippetName("TestSnippet")))
                assertThat(creatorId, equalTo(AccountId("123")))
            }

            with(get(SnippetId(2))) {
                assertThat(id, equalTo(SnippetId(2)))
                assertThat(value, equalTo(CodeValue("40 + 2")))
                assertThat(name, equalTo(SnippetName("TestSnippet")))
                assertThat(creatorId, equalTo(AccountId("123")))
            }
        }
    }

    @Nested
    inner class UpdatesSnippetTest {
        @TestFactory
        fun `Updates snippet`() = runWith(SnippetRepository::class) {
            createSnippet(
                snippetId = SnippetId(1),
                groupId = GroupId(1),
                name = SnippetName("TestSnippet")
            )

            val result = update(
                SnippetId(1),
                UpdateCmd(
                    id = CmdId(2),
                    name = SnippetName("TestSnippet2"),
                    value = CodeValue("1 + 1"),
                    inputs = SnippetInputs(MapType(mutableMapOf("answer" to NumberType(42))))
                )
            )

            with(result) {
                assertThat(id, equalTo(SnippetId(1)))
                assertThat(groupId, equalTo(GroupId(1)))
                assertThat(name, equalTo(SnippetName("TestSnippet2")))
                assertThat(value, equalTo(CodeValue("1 + 1")))
                assertThat(inputs, equalTo(SnippetInputs(MapType(mutableMapOf("answer" to NumberType(42))))))
                assertThat(creatorId, equalTo(AccountId("123")))
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Updates code multiple times`() = runWith(SnippetRepository::class) {
            createSnippet(
                snippetId = SnippetId(1),
                groupId = GroupId(1),
                name = SnippetName("TestSnippet")
            )

            repeat(13) { iteration ->
                val result = update(
                    SnippetId(1), UpdateCmd(
                        id = CmdId(iteration + 2),
                        name = SnippetName("TestSnippet$iteration"),
                        value = CodeValue("40 + $iteration"),
                    )
                )

                with(result) {
                    assertThat(id, equalTo(SnippetId(1)))
                    assertThat(groupId, equalTo(GroupId(1)))
                    assertThat(creatorId, equalTo(AccountId("123")))
                    assertThat(name, equalTo(SnippetName("TestSnippet$iteration")))
                    assertThat(value, equalTo(CodeValue("40 + $iteration")))
                }
            }
            verifyCount(1)
        }
    }

    @Nested
    inner class GetTest {
        @TestFactory
        fun `Get func by id`() = runWith(SnippetRepository::class) {
            createSnippet(
                snippetId = SnippetId(1),
                groupId = GroupId(1),
                name = SnippetName("TestSnippet"),
                value = CodeValue("1 + 1")
            )

            with(find(SnippetId(1))!!) {
                assertThat(id, equalTo(SnippetId(1)))
                assertThat(groupId, equalTo(GroupId(1)))
                assertThat(creatorId, equalTo(AccountId("123")))
                assertThat(name, equalTo(SnippetName("TestSnippet")))
                assertThat(value, equalTo(CodeValue("1 + 1")))
            }
        }

        @TestFactory
        fun `Tries to get snippet by id but does not exist`() = runWith(SnippetRepository::class) {
            val exception = assertThrows<NoSuchElementException> {
                get(SnippetId(111111))
            }
            assertThat(exception.message, equalTo("Snippet not found"))
        }
    }

    @Nested
    inner class FindTest {

        @TestFactory
        fun `Find func by id`() = runWith(SnippetRepository::class) {
            createSnippet(
                snippetId = SnippetId(1),
                groupId = GroupId(1),
                name = SnippetName("TestSnippet"),
                value = CodeValue("1 + 1")
            )

            with(find(SnippetId(1))!!) {
                assertThat(id, equalTo(SnippetId(1)))
                assertThat(groupId, equalTo(GroupId(1)))
                assertThat(creatorId, equalTo(AccountId("123")))
                assertThat(name, equalTo(SnippetName("TestSnippet")))
                assertThat(value, equalTo(CodeValue("1 + 1")))
            }
        }

        @TestFactory
        fun `Tries to find snippet by id but does not exist`() = runWith(SnippetRepository::class) {
            assertThat(find(SnippetId(111111)), nullValue())
        }
    }

    @Nested
    inner class ClearTest {
        @TestFactory
        fun `Nothing to clear`() = runWith(SnippetRepository::class) {
            clear()
            verifyCount(0)
        }

        @TestFactory
        fun `Clear table`() = runWith(SnippetRepository::class) {
            setup()
            clear()
            verifyCount(0)
        }
    }

    @Nested
    inner class ListAndCountTest {
        @TestFactory
        fun `By ids`() = runWith(SnippetRepository::class) {
            setup()

            val result = list(listOf(SnippetId(111111), SnippetId(3)))
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(SnippetId(3)))
                assertThat(groupId, equalTo(GroupId(4)))
                assertThat(name, equalTo(SnippetName("Snippet")))
            }

        }


        @TestFactory
        fun `With group ids`() = runWith(SnippetRepository::class) {
            setup()

            val query = SnippetQuery(
                groupIds = listOf(GroupId(5), GroupId(4)),
                limit = Limit(10)
            )

            assertThat(count(query), equalTo(2UL))
            val result = list(query)
            assertThat(result, hasSize(2))

            with(result[0]) {
                assertThat(id, equalTo(SnippetId(4)))
                assertThat(groupId, equalTo(GroupId(5)))
                assertThat(name, equalTo(SnippetName("Snippet")))
            }

            with(result[1]) {
                assertThat(id, equalTo(SnippetId(3)))
                assertThat(groupId, equalTo(GroupId(4)))
                assertThat(name, equalTo(SnippetName("Snippet")))
            }
        }


        @TestFactory
        fun `Limit`() = runWith(SnippetRepository::class) {
            setup()

            val query = SnippetQuery(
                groupIds = listOf(),
                limit = Limit(3)
            )

            assertThat(count(query), equalTo(4UL))
            val result = list(query)
            assertThat(result, hasSize(3))
        }

        @TestFactory
        fun `Skip and limit`() = runWith(SnippetRepository::class) {
            setup()

            val query = SnippetQuery(
                afterId = SnippetId(2),
                groupIds = listOf(),
                limit = Limit(1)
            )

            assertThat(count(query), equalTo(1UL))
            val result = list(query)
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(SnippetId(1)))
            }
        }
    }

    private fun SnippetRepository.setup() {
        createSnippet(
            snippetId = SnippetId(1),
            groupId = GroupId(3),
            name = SnippetName("Snippet")
        )

        createSnippet(
            snippetId = SnippetId(2),
            groupId = GroupId(3),
            name = SnippetName("Snippet")
        )

        createSnippet(
            snippetId = SnippetId(3),
            groupId = GroupId(4),
            name = SnippetName("Snippet")
        )

        createSnippet(
            snippetId = SnippetId(4),
            groupId = GroupId(5),
            name = SnippetName("Snippet")
        )
    }

    private fun SnippetRepository.createSnippet(
        snippetId: SnippetId,
        groupId: GroupId,
        name: SnippetName,
        value: CodeValue = CodeValue("1 + 1"),
        cmdId: CmdId = CmdId(abs(Random(10).nextInt()) + 10)
    ) {
        create(
            CreateCmd(
                id = cmdId,
                snippetId = snippetId,
                groupId = groupId,
                creatorId = AccountId("123"),
                name = name,
                inputs = SnippetInputs(
                    MapType(
                        mutableMapOf(
                            "hamal" to StringType("rockz")
                        )
                    )
                ),
                value = value
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