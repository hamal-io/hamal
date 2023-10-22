package io.hamal.repository

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.ExtensionCmdRepository.*
import io.hamal.repository.api.ExtensionCode
import io.hamal.repository.api.ExtensionRepository
import io.hamal.repository.fixture.AbstractUnitTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory

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


        }
    }
}