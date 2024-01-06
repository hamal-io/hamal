package io.hamal.repository

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.type.KuaMap
import io.hamal.lib.kua.type.KuaNumber
import io.hamal.lib.kua.type.KuaString
import io.hamal.repository.api.BlueprintCmdRepository.CreateCmd
import io.hamal.repository.api.BlueprintCmdRepository.UpdateCmd
import io.hamal.repository.api.BlueprintQueryRepository.BlueprintQuery
import io.hamal.repository.api.BlueprintRepository
import io.hamal.repository.fixture.AbstractUnitTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import kotlin.math.abs
import kotlin.random.Random

class BlueprintRepositoryTest : AbstractUnitTest() {

    @Nested
    inner class CreatesBlueprintTest {

        @TestFactory
        fun `Creates Blueprint`() = runWith(BlueprintRepository::class) {
            val result = create(
                CreateCmd(
                    id = CmdId(1),
                    blueprintId = BlueprintId(123),
                    groupId = GroupId(1),
                    creatorId = AccountId("123"),
                    name = BlueprintName("TestBlueprint"),
                    inputs = BlueprintInputs(
                        KuaMap(
                            mutableMapOf(
                                "hamal" to KuaString("rockz")
                            )
                        )
                    ),
                    value = CodeValue("1 + 1")
                )
            )

            with(result) {
                assertThat(id, equalTo(BlueprintId(123)))
                assertThat(groupId, equalTo(GroupId(1)))
                assertThat(creatorId, equalTo(AccountId("123")))
                assertThat(name, equalTo(BlueprintName("TestBlueprint")))
                assertThat(inputs, equalTo(BlueprintInputs(KuaMap(mutableMapOf("hamal" to KuaString("rockz"))))))
                assertThat(value, equalTo(CodeValue("1 + 1")))
            }
            verifyCount(1)
        }

        @TestFactory
        fun `Creates Blueprint duplicate`() = runWith(BlueprintRepository::class) {
            createBlueprint(
                blueprintId = BlueprintId(1),
                groupId = GroupId(1),
                name = BlueprintName("TestBlueprint"),
                value = CodeValue("40 + 2")
            )

            createBlueprint(
                blueprintId = BlueprintId(2),
                groupId = GroupId(1),
                name = BlueprintName("TestBlueprint"),
                value = CodeValue("40 + 2")
            )

            verifyCount(2)

            with(get(BlueprintId(1))) {
                assertThat(id, equalTo(BlueprintId(1)))
                assertThat(value, equalTo(CodeValue("40 + 2")))
                assertThat(name, equalTo(BlueprintName("TestBlueprint")))
                assertThat(creatorId, equalTo(AccountId("123")))
            }

            with(get(BlueprintId(2))) {
                assertThat(id, equalTo(BlueprintId(2)))
                assertThat(value, equalTo(CodeValue("40 + 2")))
                assertThat(name, equalTo(BlueprintName("TestBlueprint")))
                assertThat(creatorId, equalTo(AccountId("123")))
            }
        }
    }

    @Nested
    inner class UpdatesBlueprintTest {
        @TestFactory
        fun `Updates Blueprint`() = runWith(BlueprintRepository::class) {
            createBlueprint(
                blueprintId = BlueprintId(1),
                groupId = GroupId(1),
                name = BlueprintName("TestBlueprint")
            )

            val result = update(
                BlueprintId(1),
                UpdateCmd(
                    id = CmdId(2),
                    name = BlueprintName("TestBlueprint2"),
                    value = CodeValue("1 + 1"),
                    inputs = BlueprintInputs(KuaMap(mutableMapOf("answer" to KuaNumber(42))))
                )
            )

            with(result) {
                assertThat(id, equalTo(BlueprintId(1)))
                assertThat(groupId, equalTo(GroupId(1)))
                assertThat(name, equalTo(BlueprintName("TestBlueprint2")))
                assertThat(value, equalTo(CodeValue("1 + 1")))
                assertThat(inputs, equalTo(BlueprintInputs(KuaMap(mutableMapOf("answer" to KuaNumber(42))))))
                assertThat(creatorId, equalTo(AccountId("123")))
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Updates blueprint multiple times`() = runWith(BlueprintRepository::class) {
            createBlueprint(
                blueprintId = BlueprintId(1),
                groupId = GroupId(1),
                name = BlueprintName("TestBlueprint")
            )

            repeat(13) { iteration ->
                val result = update(
                    BlueprintId(1), UpdateCmd(
                        id = CmdId(iteration + 2),
                        name = BlueprintName("TestBlueprint$iteration"),
                        value = CodeValue("40 + $iteration"),
                    )
                )

                with(result) {
                    assertThat(id, equalTo(BlueprintId(1)))
                    assertThat(groupId, equalTo(GroupId(1)))
                    assertThat(creatorId, equalTo(AccountId("123")))
                    assertThat(name, equalTo(BlueprintName("TestBlueprint$iteration")))
                    assertThat(value, equalTo(CodeValue("40 + $iteration")))
                }
            }
            verifyCount(1)
        }
    }

    @Nested
    inner class GetTest {
        @TestFactory
        fun `Get blueprint by id`() = runWith(BlueprintRepository::class) {
            createBlueprint(
                blueprintId = BlueprintId(1),
                groupId = GroupId(1),
                name = BlueprintName("TestBlueprint"),
                value = CodeValue("1 + 1")
            )

            with(find(BlueprintId(1))!!) {
                assertThat(id, equalTo(BlueprintId(1)))
                assertThat(groupId, equalTo(GroupId(1)))
                assertThat(creatorId, equalTo(AccountId("123")))
                assertThat(name, equalTo(BlueprintName("TestBlueprint")))
                assertThat(value, equalTo(CodeValue("1 + 1")))
            }
        }

        @TestFactory
        fun `Tries to get blueprint by id but does not exist`() = runWith(BlueprintRepository::class) {
            val exception = assertThrows<NoSuchElementException> {
                get(BlueprintId(111111))
            }
            assertThat(exception.message, equalTo("Blueprint not found"))
        }
    }

    @Nested
    inner class FindTest {

        @TestFactory
        fun `Find blueprint by id`() = runWith(BlueprintRepository::class) {
            createBlueprint(
                blueprintId = BlueprintId(1),
                groupId = GroupId(1),
                name = BlueprintName("TestBlueprint"),
                value = CodeValue("1 + 1")
            )

            with(find(BlueprintId(1))!!) {
                assertThat(id, equalTo(BlueprintId(1)))
                assertThat(groupId, equalTo(GroupId(1)))
                assertThat(creatorId, equalTo(AccountId("123")))
                assertThat(name, equalTo(BlueprintName("TestBlueprint")))
                assertThat(value, equalTo(CodeValue("1 + 1")))
            }
        }

        @TestFactory
        fun `Tries to find Blueprint by id but does not exist`() = runWith(BlueprintRepository::class) {
            assertThat(find(BlueprintId(111111)), nullValue())
        }
    }

    @Nested
    inner class ClearTest {
        @TestFactory
        fun `Nothing to clear`() = runWith(BlueprintRepository::class) {
            clear()
            verifyCount(0)
        }

        @TestFactory
        fun `Clear table`() = runWith(BlueprintRepository::class) {
            setup()
            clear()
            verifyCount(0)
        }
    }

    @Nested
    inner class ListAndCountTest {
        @TestFactory
        fun `By ids`() = runWith(BlueprintRepository::class) {
            setup()

            val result = list(listOf(BlueprintId(111111), BlueprintId(3)))
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(BlueprintId(3)))
                assertThat(groupId, equalTo(GroupId(4)))
                assertThat(name, equalTo(BlueprintName("Blueprint")))
            }

        }


        @TestFactory
        fun `With group ids`() = runWith(BlueprintRepository::class) {
            setup()

            val query = BlueprintQuery(
                groupIds = listOf(GroupId(5), GroupId(4)),
                limit = Limit(10)
            )

            assertThat(count(query), equalTo(2UL))
            val result = list(query)
            assertThat(result, hasSize(2))

            with(result[0]) {
                assertThat(id, equalTo(BlueprintId(4)))
                assertThat(groupId, equalTo(GroupId(5)))
                assertThat(name, equalTo(BlueprintName("Blueprint")))
            }

            with(result[1]) {
                assertThat(id, equalTo(BlueprintId(3)))
                assertThat(groupId, equalTo(GroupId(4)))
                assertThat(name, equalTo(BlueprintName("Blueprint")))
            }
        }


        @TestFactory
        fun `Limit`() = runWith(BlueprintRepository::class) {
            setup()

            val query = BlueprintQuery(
                groupIds = listOf(),
                limit = Limit(3)
            )

            assertThat(count(query), equalTo(4UL))
            val result = list(query)
            assertThat(result, hasSize(3))
        }

        @TestFactory
        fun `Skip and limit`() = runWith(BlueprintRepository::class) {
            setup()

            val query = BlueprintQuery(
                afterId = BlueprintId(2),
                groupIds = listOf(),
                limit = Limit(1)
            )

            assertThat(count(query), equalTo(1UL))
            val result = list(query)
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(BlueprintId(1)))
            }
        }
    }

    private fun BlueprintRepository.setup() {
        createBlueprint(
            blueprintId = BlueprintId(1),
            groupId = GroupId(3),
            name = BlueprintName("Blueprint")
        )

        createBlueprint(
            blueprintId = BlueprintId(2),
            groupId = GroupId(3),
            name = BlueprintName("Blueprint")
        )

        createBlueprint(
            blueprintId = BlueprintId(3),
            groupId = GroupId(4),
            name = BlueprintName("Blueprint")
        )

        createBlueprint(
            blueprintId = BlueprintId(4),
            groupId = GroupId(5),
            name = BlueprintName("Blueprint")
        )
    }

    private fun BlueprintRepository.createBlueprint(
        blueprintId: BlueprintId,
        groupId: GroupId,
        name: BlueprintName,
        value: CodeValue = CodeValue("1 + 1"),
        cmdId: CmdId = CmdId(abs(Random(10).nextInt()) + 10)
    ) {
        create(
            CreateCmd(
                id = cmdId,
                blueprintId = blueprintId,
                groupId = groupId,
                creatorId = AccountId("123"),
                name = name,
                inputs = BlueprintInputs(
                    KuaMap(
                        mutableMapOf(
                            "hamal" to KuaString("rockz")
                        )
                    )
                ),
                value = value
            )
        )
    }

    private fun BlueprintRepository.verifyCount(expected: Int) {
        verifyCount(expected) {}
    }

    private fun BlueprintRepository.verifyCount(expected: Int, block: BlueprintQuery.() -> Unit) {
        val counted = count(BlueprintQuery(groupIds = listOf()).also(block))
        assertThat("number of blueprints expected", counted, equalTo(expected.toULong()))
    }
}