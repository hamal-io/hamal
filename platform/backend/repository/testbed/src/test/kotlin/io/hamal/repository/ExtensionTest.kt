package io.hamal.repository

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.*
import io.hamal.repository.api.ExtensionCmdRepository.*
import io.hamal.repository.api.ExtensionQueryRepository.*
import io.hamal.repository.fixture.AbstractUnitTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalArgumentException
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
                    extId = ExtensionId(123),
                    groupId = GroupId(1),
                    name = ExtensionName("Extension1"),
                    code = ExtensionCode(
                        id = CodeId(5),
                        version = CodeVersion(6)
                    )
                )
            )

            with(result) {
                assertThat(id, equalTo(ExtensionId(123)))
                assertThat(groupId, equalTo(GroupId(1)))
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
        fun `Tries to create extension but name already exists in group`() =
            runWith(ExtensionRepository::class) {
                createExtension(
                    extId = ExtensionId(1),
                    groupId = GroupId(1),
                    name = ExtensionName("TestExt")
                )

                val exception = assertThrows<IllegalArgumentException> {
                    create(
                        CreateCmd(
                            id = CmdId(2),
                            extId = ExtensionId(5),
                            groupId = GroupId(1),
                            name = ExtensionName("TestExt"),
                            code = ExtensionCode(
                                id = CodeId(5),
                                version = CodeVersion(6)
                            )
                        )
                    )
                }

                assertThat(
                    exception.message,
                    equalTo("ExtensionName(TestExt) already exists in group GroupId(1)")
                )
                verifyCount(1)
            }
    }


    @Nested
    inner class UpdatesExtensionTest {}

    @Nested
    inner class ClearTest {}

    @Nested
    inner class GetTest {}

    @Nested
    inner class FindTest {}

    @Nested
    inner class ListAndCountTest {}


}

private fun ExtensionRepository.createExtension(
    extId: ExtensionId,
    name: ExtensionName,
    groupId: GroupId,
    codeId: CodeId = CodeId(5),
    codeVersion: CodeVersion = CodeVersion(6),
    cmdId: CmdId = CmdId(abs(Random(10).nextInt()) + 10)
) {
    create(
        CreateCmd(
            id = cmdId,
            extId = extId,
            groupId = groupId,
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
    val counted = count(ExtensionQuery(groupIds = listOf()).also(block))
    assertThat("number of extensions expected", counted, equalTo(expected.toULong()))
}