package io.hamal.repository

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.NumberType
import io.hamal.lib.kua.type.StringType
import io.hamal.repository.api.FuncCmdRepository.CreateCmd
import io.hamal.repository.api.FuncCmdRepository.UpdateCmd
import io.hamal.repository.api.FuncQueryRepository.FuncQuery
import io.hamal.repository.api.FuncRepository
import io.hamal.repository.fixture.AbstractUnitTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import kotlin.math.abs
import kotlin.random.Random

internal class FuncRepositoryTest : AbstractUnitTest() {

    @Nested
    inner class CreateFuncTest {
        @TestFactory
        fun `Creates Func`() = runWith(FuncRepository::class) { testInstance ->
            val result = testInstance.create(
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
                    code = CodeType("40 + 2")
                )
            )

            with(result) {
                assertThat(id, equalTo(FuncId(123)))
                assertThat(groupId, equalTo(GroupId(1)))
                assertThat(namespaceId, equalTo(NamespaceId(234)))
                assertThat(name, equalTo(FuncName("SomeFunc")))
                assertThat(inputs, equalTo(FuncInputs(MapType(mutableMapOf("hamal" to StringType("rockz"))))))
                assertThat(code, equalTo(CodeType("40 + 2")))
            }

            testInstance.verifyCount(1)
        }

        @TestFactory
        fun `Tries to create but same name already exist in namespace`() =
            runWith(FuncRepository::class) { testInstance ->

                testInstance.createFunc(
                    funcId = FuncId(1),
                    namespaceId = NamespaceId(2),
                    groupId = GroupId(3),
                    name = FuncName("first-func-name")
                )

                val exception = assertThrows<IllegalArgumentException> {
                    testInstance.create(
                        CreateCmd(
                            id = CmdId(2),
                            funcId = FuncId(4),
                            groupId = GroupId(3),
                            namespaceId = NamespaceId(2),
                            name = FuncName("first-func-name"),
                            inputs = FuncInputs(),
                            code = CodeType("40 + 2")
                        )
                    )
                }

                assertThat(
                    exception.message,
                    equalTo("FuncName(first-func-name) not unique in namespace NamespaceId(2)")
                )

                testInstance.verifyCount(1)
            }

        @TestFactory
        fun `Creates with same name but different namespace`() =
            runWith(FuncRepository::class) { testInstance ->

                testInstance.createFunc(
                    funcId = FuncId(1),
                    namespaceId = NamespaceId(2),
                    groupId = GroupId(3),
                    name = FuncName("func-name")
                )

                val result = testInstance.create(
                    CreateCmd(
                        id = CmdId(2),
                        funcId = FuncId(4),
                        groupId = GroupId(3),
                        namespaceId = NamespaceId(22),
                        name = FuncName("func-name"),
                        inputs = FuncInputs(),
                        code = CodeType("'13'..'37'")
                    )
                )

                with(result) {
                    assertThat(id, equalTo(FuncId(4)))
                    assertThat(groupId, equalTo(GroupId(3)))
                    assertThat(namespaceId, equalTo(NamespaceId(22)))
                    assertThat(name, equalTo(FuncName("func-name")))
                    assertThat(inputs, equalTo(FuncInputs()))
                    assertThat(code, equalTo(CodeType("'13'..'37'")))
                }

                testInstance.verifyCount(2)
            }

        @TestFactory
        fun `Tries to create but cmd with func id was already applied`() =
            runWith(FuncRepository::class) { testInstance ->

                testInstance.createFunc(
                    cmdId = CmdId(23456),
                    funcId = FuncId(5),
                    namespaceId = NamespaceId(2),
                    groupId = GroupId(3),
                    name = FuncName("first-func-name")
                )


                val result = testInstance.create(
                    CreateCmd(
                        id = CmdId(23456),
                        funcId = FuncId(5),
                        groupId = GroupId(333),
                        namespaceId = NamespaceId(2222),
                        name = FuncName("second-func-name"),
                        inputs = FuncInputs(),
                        code = CodeType("40 + 2")
                    )
                )

                with(result) {
                    assertThat(id, equalTo(FuncId(5)))
                    assertThat(groupId, equalTo(GroupId(3)))
                    assertThat(namespaceId, equalTo(NamespaceId(2)))
                    assertThat(name, equalTo(FuncName("first-func-name")))
                    assertThat(inputs, equalTo(FuncInputs(MapType(mutableMapOf("hamal" to StringType("rockz"))))))
                    assertThat(code, equalTo(CodeType("40 + 2")))
                }

                testInstance.verifyCount(1)
            }
    }

    @Nested
    inner class UpdatesFuncTest {

        @TestFactory
        fun `Updates func`() = runWith(FuncRepository::class) { testInstance ->
            testInstance.createFunc(
                funcId = FuncId(1),
                namespaceId = NamespaceId(2),
                groupId = GroupId(3),
                name = FuncName("func-name")
            )

            val result = testInstance.update(
                FuncId(1), UpdateCmd(
                    id = CmdId(2),
                    namespaceId = NamespaceId(22),
                    name = FuncName("Updated"),
                    inputs = FuncInputs(MapType(mutableMapOf("answer" to NumberType(42)))),
                    code = CodeType("'13'..'37'")
                )
            )

            with(result) {
                assertThat(id, equalTo(FuncId(1)))
                assertThat(groupId, equalTo(GroupId(3)))
                assertThat(namespaceId, equalTo(NamespaceId(22)))
                assertThat(name, equalTo(FuncName("Updated")))
                assertThat(inputs, equalTo(FuncInputs(MapType(mutableMapOf("answer" to NumberType(42))))))
                assertThat(code, equalTo(CodeType("'13'..'37'")))
            }

            testInstance.verifyCount(1)
        }

        @TestFactory
        fun `Updates func without updating it`() = runWith(FuncRepository::class) { testInstance ->
            testInstance.createFunc(
                funcId = FuncId(1),
                namespaceId = NamespaceId(2),
                groupId = GroupId(3),
                name = FuncName("func-name")
            )

            val result = testInstance.update(
                FuncId(1), UpdateCmd(
                    id = CmdId(2),
                    namespaceId = null,
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
                assertThat(code, equalTo(CodeType("40 + 2")))
            }

            testInstance.verifyCount(1)
        }

        @TestFactory
        fun `Tries to update but same name already exist in namespace`() =
            runWith(FuncRepository::class) { testInstance ->

                testInstance.createFunc(
                    funcId = FuncId(1),
                    namespaceId = NamespaceId(2),
                    groupId = GroupId(3),
                    name = FuncName("already-exists")
                )

                testInstance.createFunc(
                    funcId = FuncId(2),
                    namespaceId = NamespaceId(2),
                    groupId = GroupId(3),
                    name = FuncName("to-update")
                )

                val exception = assertThrows<IllegalArgumentException> {
                    testInstance.update(
                        FuncId(2), UpdateCmd(
                            id = CmdId(2),
                            name = FuncName("already-exists"),
                        )
                    )
                }

                assertThat(
                    exception.message,
                    equalTo("FuncName(already-exists) not unique in namespace NamespaceId(2)")
                )

                with(testInstance.get(FuncId(2))) {
                    assertThat(id, equalTo(FuncId(2)))
                    assertThat(namespaceId, equalTo(NamespaceId(2)))
                    assertThat(groupId, equalTo(GroupId(3)))
                    assertThat(name, equalTo(FuncName("to-update")))
                }

                testInstance.verifyCount(2)
            }

    }

    @Nested
    inner class ClearTest {

        @TestFactory
        fun `Nothing to clear`() = runWith(FuncRepository::class) { testInstance ->
            testInstance.clear()
            testInstance.verifyCount(0)
        }


        @TestFactory
        fun `Clear table`() = runWith(FuncRepository::class) { testInstance ->

            testInstance.createFunc(
                funcId = FuncId(1),
                namespaceId = NamespaceId(2),
                groupId = GroupId(3),
                name = FuncName("already-exists")
            )

            testInstance.createFunc(
                funcId = FuncId(2),
                namespaceId = NamespaceId(2),
                groupId = GroupId(3),
                name = FuncName("to-update")
            )
            testInstance.clear()
            testInstance.verifyCount(0)
        }

    }
}


private fun FuncRepository.createFunc(
    funcId: FuncId,
    namespaceId: NamespaceId,
    name: FuncName,
    groupId: GroupId,
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
            code = CodeType("40 + 2")
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