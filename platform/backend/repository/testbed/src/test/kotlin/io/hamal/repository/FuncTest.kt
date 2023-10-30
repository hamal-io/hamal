package io.hamal.repository

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.NumberType
import io.hamal.lib.kua.type.StringType
import io.hamal.repository.api.FuncCmdRepository.CreateCmd
import io.hamal.repository.api.FuncCmdRepository.UpdateCmd
import io.hamal.repository.api.FuncCode
import io.hamal.repository.api.FuncQueryRepository.FuncQuery
import io.hamal.repository.api.FuncRepository
import io.hamal.repository.fixture.AbstractUnitTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import kotlin.math.abs
import kotlin.random.Random

internal class FuncRepositoryTest : AbstractUnitTest() {

    @Nested
    inner class CreateFuncTest {
        @TestFactory
        fun `Creates Func`() = runWith(FuncRepository::class) {
            val result = create(
                CreateCmd(
                    id = CmdId(1),
                    funcId = FuncId(123),
                    groupId = GroupId(1),
                    namespaceId = NamespaceId(234),
                    name = FuncName("SomeFunc"),
                    inputs = FuncInputs(
                        MapType(
                            mutableMapOf(
                                "hamal" to StringType("rockz")
                            )
                        )
                    ),
                    code = FuncCode(
                        id = CodeId(5),
                        version = CodeVersion(6),
                        deployedVersion = CodeVersion(1)
                    ),
                )
            )

            with(result) {
                assertThat(id, equalTo(FuncId(123)))
                assertThat(groupId, equalTo(GroupId(1)))
                assertThat(namespaceId, equalTo(NamespaceId(234)))
                assertThat(name, equalTo(FuncName("SomeFunc")))
                assertThat(inputs, equalTo(FuncInputs(MapType(mutableMapOf("hamal" to StringType("rockz"))))))
                assertThat(
                    code, equalTo(
                        FuncCode(
                            id = CodeId(5),
                            version = CodeVersion(6),
                            deployedVersion = CodeVersion(1) // FIXME-53 should be == version
                        )
                    )
                )
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Tries to create but same name already exists in namespace`() =
            runWith(FuncRepository::class) {

                createFunc(
                    funcId = FuncId(1),
                    namespaceId = NamespaceId(2),
                    groupId = GroupId(3),
                    name = FuncName("first-func-name")
                )

                val exception = assertThrows<IllegalArgumentException> {
                    create(
                        CreateCmd(
                            id = CmdId(2),
                            funcId = FuncId(4),
                            groupId = GroupId(3),
                            namespaceId = NamespaceId(2),
                            name = FuncName("first-func-name"),
                            inputs = FuncInputs(),
                            code = FuncCode(
                                id = CodeId(5),
                                version = CodeVersion(6),
                                deployedVersion = CodeVersion(1)
                            )
                        )
                    )
                }

                assertThat(
                    exception.message,
                    equalTo("FuncName(first-func-name) already exists in namespace NamespaceId(2)")
                )

                verifyCount(1)
            }

        @TestFactory
        fun `Creates with same name but different namespace`() =
            runWith(FuncRepository::class) {

                createFunc(
                    funcId = FuncId(1),
                    namespaceId = NamespaceId(2),
                    groupId = GroupId(3),
                    name = FuncName("func-name")
                )

                val result = create(
                    CreateCmd(
                        id = CmdId(2),
                        funcId = FuncId(4),
                        groupId = GroupId(3),
                        namespaceId = NamespaceId(22),
                        name = FuncName("func-name"),
                        inputs = FuncInputs(),
                        code = FuncCode(
                            id = CodeId(5),
                            version = CodeVersion(6),
                            deployedVersion = CodeVersion(1)
                        )
                    )
                )

                with(result) {
                    assertThat(id, equalTo(FuncId(4)))
                    assertThat(groupId, equalTo(GroupId(3)))
                    assertThat(namespaceId, equalTo(NamespaceId(22)))
                    assertThat(name, equalTo(FuncName("func-name")))
                    assertThat(inputs, equalTo(FuncInputs()))
                    assertThat(
                        code, equalTo(
                            FuncCode(
                                id = CodeId(5),
                                version = CodeVersion(6),
                                deployedVersion = CodeVersion(1)
                            )
                        )
                    )
                }

                verifyCount(2)
            }

        @TestFactory
        fun `Tries to create but cmd with func id was already applied`() =
            runWith(FuncRepository::class) {

                createFunc(
                    cmdId = CmdId(23456),
                    funcId = FuncId(5),
                    namespaceId = NamespaceId(2),
                    groupId = GroupId(3),
                    name = FuncName("first-func-name")
                )


                val result = create(
                    CreateCmd(
                        id = CmdId(23456),
                        funcId = FuncId(5),
                        groupId = GroupId(333),
                        namespaceId = NamespaceId(2222),
                        name = FuncName("second-func-name"),
                        inputs = FuncInputs(),
                        code = FuncCode(
                            id = CodeId(5),
                            version = CodeVersion(6),
                            deployedVersion = CodeVersion(1)
                        )
                    )
                )

                with(result) {
                    assertThat(id, equalTo(FuncId(5)))
                    assertThat(groupId, equalTo(GroupId(3)))
                    assertThat(namespaceId, equalTo(NamespaceId(2)))
                    assertThat(name, equalTo(FuncName("first-func-name")))
                    assertThat(inputs, equalTo(FuncInputs(MapType(mutableMapOf("hamal" to StringType("rockz"))))))
                    assertThat(
                        code, equalTo(
                            FuncCode(
                                id = CodeId(5),
                                version = CodeVersion(6),
                                deployedVersion = CodeVersion(1)
                            )
                        )
                    )
                }

                verifyCount(1)
            }
    }

    @Nested
    inner class UpdatesFuncTest {

        @TestFactory
        fun `Updates func`() = runWith(FuncRepository::class) {
            createFunc(
                funcId = FuncId(1),
                namespaceId = NamespaceId(2),
                groupId = GroupId(3),
                name = FuncName("func-name")
            )

            val result = update(
                FuncId(1), UpdateCmd(
                    id = CmdId(2),
                    name = FuncName("Updated"),
                    inputs = FuncInputs(MapType(mutableMapOf("answer" to NumberType(42)))),
                    code = FuncCode(
                        id = CodeId(5),
                        version = CodeVersion(6),
                        deployedVersion = CodeVersion(1)
                    )
                )
            )

            with(result) {
                assertThat(id, equalTo(FuncId(1)))
                assertThat(groupId, equalTo(GroupId(3)))
                assertThat(namespaceId, equalTo(NamespaceId(2)))
                assertThat(name, equalTo(FuncName("Updated")))
                assertThat(inputs, equalTo(FuncInputs(MapType(mutableMapOf("answer" to NumberType(42))))))
                assertThat(
                    code, equalTo(
                        FuncCode(
                            id = CodeId(5),
                            version = CodeVersion(6),
                            deployedVersion = CodeVersion(1)
                        )
                    )
                )
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Updates func without updating it`() = runWith(FuncRepository::class) {
            createFunc(
                funcId = FuncId(1),
                namespaceId = NamespaceId(2),
                groupId = GroupId(3),
                name = FuncName("func-name")
            )

            val result = update(
                FuncId(1), UpdateCmd(
                    id = CmdId(2),
                    name = null,
                    inputs = null,
                    code = null
                )
            )

            with(result) {
                assertThat(id, equalTo(FuncId(1)))
                assertThat(groupId, equalTo(GroupId(3)))
                assertThat(namespaceId, equalTo(NamespaceId(2)))
                assertThat(name, equalTo(FuncName("func-name")))
                assertThat(inputs, equalTo(FuncInputs(MapType(mutableMapOf("hamal" to StringType("rockz"))))))
                assertThat(
                    code, equalTo(
                        FuncCode(
                            id = CodeId(5),
                            version = CodeVersion(6),
                            deployedVersion = CodeVersion(1)
                        )
                    )
                )
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Tries to update but same name already exists in namespace`() =
            runWith(FuncRepository::class) {

                createFunc(
                    funcId = FuncId(1),
                    namespaceId = NamespaceId(2),
                    groupId = GroupId(3),
                    name = FuncName("already-exists")
                )

                createFunc(
                    funcId = FuncId(2),
                    namespaceId = NamespaceId(2),
                    groupId = GroupId(3),
                    name = FuncName("to-update")
                )

                val exception = assertThrows<IllegalArgumentException> {
                    update(
                        FuncId(2), UpdateCmd(
                            id = CmdId(2),
                            name = FuncName("already-exists"),
                        )
                    )
                }

                assertThat(
                    exception.message,
                    equalTo("FuncName(already-exists) already exists in namespace NamespaceId(2)")
                )

                with(get(FuncId(2))) {
                    assertThat(id, equalTo(FuncId(2)))
                    assertThat(namespaceId, equalTo(NamespaceId(2)))
                    assertThat(groupId, equalTo(GroupId(3)))
                    assertThat(name, equalTo(FuncName("to-update")))
                }

                verifyCount(2)
            }

    }

    // FIXME-53 Deploy tests

    @Nested
    inner class ClearTest {

        @TestFactory
        fun `Nothing to clear`() = runWith(FuncRepository::class) {
            clear()
            verifyCount(0)
        }

        @TestFactory
        fun `Clear table`() = runWith(FuncRepository::class) {

            createFunc(
                funcId = FuncId(1),
                namespaceId = NamespaceId(2),
                groupId = GroupId(3),
                name = FuncName("already-exists")
            )

            createFunc(
                funcId = FuncId(2),
                namespaceId = NamespaceId(2),
                groupId = GroupId(3),
                name = FuncName("to-update")
            )

            clear()
            verifyCount(0)
        }

    }

    @Nested
    inner class GetTest {
        @TestFactory
        fun `Get func by id`() = runWith(FuncRepository::class) {
            createFunc(
                funcId = FuncId(1),
                namespaceId = NamespaceId(2),
                groupId = GroupId(3),
                name = FuncName("SomeFunc"),
                codeId = CodeId(4),
                codeVersion = CodeVersion(5)
            )

            with(get(FuncId(1))) {
                assertThat(id, equalTo(FuncId(1)))
                assertThat(groupId, equalTo(GroupId(3)))
                assertThat(namespaceId, equalTo(NamespaceId(2)))
                assertThat(name, equalTo(FuncName("SomeFunc")))
                assertThat(inputs, equalTo(FuncInputs(MapType(mutableMapOf("hamal" to StringType("rockz"))))))
                assertThat(
                    code, equalTo(
                        FuncCode(
                            id = CodeId(4),
                            version = CodeVersion(5),
                            deployedVersion = CodeVersion(1)
                        )
                    )
                )
            }
        }

        @TestFactory
        fun `Tries to get func by id but does not exist`() = runWith(FuncRepository::class) {
            createFunc(
                funcId = FuncId(1),
                namespaceId = NamespaceId(2),
                groupId = GroupId(3),
                name = FuncName("SomeFunc")
            )

            val exception = assertThrows<NoSuchElementException> {
                get(FuncId(111111))
            }
            assertThat(exception.message, equalTo("Func not found"))
        }
    }

    @Nested
    inner class FindTest {
        @TestFactory
        fun `Find func by id`() = runWith(FuncRepository::class) {
            createFunc(
                funcId = FuncId(1),
                namespaceId = NamespaceId(2),
                groupId = GroupId(3),
                name = FuncName("SomeFunc"),
                codeId = CodeId(4),
                codeVersion = CodeVersion(5)
            )

            with(find(FuncId(1))!!) {
                assertThat(id, equalTo(FuncId(1)))
                assertThat(groupId, equalTo(GroupId(3)))
                assertThat(namespaceId, equalTo(NamespaceId(2)))
                assertThat(name, equalTo(FuncName("SomeFunc")))
                assertThat(inputs, equalTo(FuncInputs(MapType(mutableMapOf("hamal" to StringType("rockz"))))))
                assertThat(
                    code, equalTo(
                        FuncCode(
                            id = CodeId(4),
                            version = CodeVersion(5),
                            deployedVersion = CodeVersion(1)
                        )
                    )
                )
            }
        }

        @TestFactory
        fun `Tries to find func by id but does not exist`() = runWith(FuncRepository::class) {
            createFunc(
                funcId = FuncId(1),
                namespaceId = NamespaceId(2),
                groupId = GroupId(3),
                name = FuncName("SomeFunc")
            )

            val result = find(FuncId(111111))
            assertThat(result, nullValue())
        }
    }

    @Nested
    inner class ListAndCountTest {

        @TestFactory
        fun `By ids`() = runWith(FuncRepository::class) {
            setup()

            val result = list(listOf(FuncId(111111), FuncId(3)))
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(FuncId(3)))
                assertThat(namespaceId, equalTo(NamespaceId(4)))
                assertThat(groupId, equalTo(GroupId(4)))
                assertThat(name, equalTo(FuncName("Func")))
            }
        }

        @TestFactory
        fun `With group ids`() = runWith(FuncRepository::class) {
            setup()

            val query = FuncQuery(
                groupIds = listOf(GroupId(5), GroupId(4)),
                limit = Limit(10)
            )

            assertThat(count(query), equalTo(2UL))
            val result = list(query)
            assertThat(result, hasSize(2))

            with(result[0]) {
                assertThat(id, equalTo(FuncId(4)))
                assertThat(namespaceId, equalTo(NamespaceId(10)))
                assertThat(groupId, equalTo(GroupId(5)))
                assertThat(name, equalTo(FuncName("Func")))
            }

            with(result[1]) {
                assertThat(id, equalTo(FuncId(3)))
                assertThat(namespaceId, equalTo(NamespaceId(4)))
                assertThat(groupId, equalTo(GroupId(4)))
                assertThat(name, equalTo(FuncName("Func")))
            }
        }

        @TestFactory
        fun `With namespace ids`() = runWith(FuncRepository::class) {
            setup()

            val query = FuncQuery(
                namespaceIds = listOf(NamespaceId(10), NamespaceId(12)),
                groupIds = listOf(),
                limit = Limit(10)
            )

            assertThat(count(query), equalTo(1UL))
            val result = list(query)
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(FuncId(4)))
                assertThat(namespaceId, equalTo(NamespaceId(10)))
                assertThat(groupId, equalTo(GroupId(5)))
                assertThat(name, equalTo(FuncName("Func")))
            }
        }

        @TestFactory
        fun `Limit`() = runWith(FuncRepository::class) {
            setup()

            val query = FuncQuery(
                groupIds = listOf(),
                limit = Limit(3)
            )

            assertThat(count(query), equalTo(4UL))
            val result = list(query)
            assertThat(result, hasSize(3))
        }

        @TestFactory
        fun `Skip and limit`() = runWith(FuncRepository::class) {
            setup()

            val query = FuncQuery(
                afterId = FuncId(2),
                groupIds = listOf(),
                limit = Limit(1)
            )

            assertThat(count(query), equalTo(1UL))
            val result = list(query)
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(FuncId(1)))
            }
        }

        private fun FuncRepository.setup() {
            createFunc(
                funcId = FuncId(1),
                namespaceId = NamespaceId(2),
                groupId = GroupId(3),
                name = FuncName("Func")
            )

            createFunc(
                funcId = FuncId(2),
                namespaceId = NamespaceId(3),
                groupId = GroupId(3),
                name = FuncName("Func")
            )

            createFunc(
                funcId = FuncId(3),
                namespaceId = NamespaceId(4),
                groupId = GroupId(4),
                name = FuncName("Func")
            )

            createFunc(
                funcId = FuncId(4),
                namespaceId = NamespaceId(10),
                groupId = GroupId(5),
                name = FuncName("Func")
            )
        }
    }
}

private fun FuncRepository.createFunc(
    funcId: FuncId,
    namespaceId: NamespaceId,
    name: FuncName,
    groupId: GroupId,
    codeId: CodeId = CodeId(5),
    codeVersion: CodeVersion = CodeVersion(6),
    cmdId: CmdId = CmdId(abs(Random(10).nextInt()) + 10)
) {
    create(
        CreateCmd(
            id = cmdId,
            funcId = funcId,
            groupId = groupId,
            namespaceId = namespaceId,
            name = name,
            inputs = FuncInputs(
                MapType(
                    mutableMapOf(
                        "hamal" to StringType("rockz")
                    )
                )
            ),
            code = FuncCode(
                id = codeId,
                version = codeVersion,
                deployedVersion = CodeVersion(1)
            )
        )
    )
}

private fun FuncRepository.verifyCount(expected: Int) {
    verifyCount(expected) { }
}

private fun FuncRepository.verifyCount(expected: Int, block: FuncQuery.() -> Unit) {
    val counted = count(FuncQuery(groupIds = listOf()).also(block))
    assertThat("number of functions expected", counted, equalTo(expected.toULong()))
}