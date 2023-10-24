package io.hamal.api.http.extension

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.lib.domain.vo.ExtensionName
import io.hamal.lib.sdk.api.ApiCreateExtensionReq
import io.hamal.lib.sdk.api.ApiExtensionList
import io.hamal.repository.api.ExtensionCmdRepository
import io.hamal.repository.api.ExtensionCode
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test

internal class ExtensionListControllerTest : ExtensionBaseControllerTest() {
    @Test
    fun `No extentions`() {
        assertThat(getExtensionList().extensions, equalTo(emptyList()))
    }

    @Test
    fun `Single Extension`() {
        extensionCmdRepository.create(
            ExtensionCmdRepository.CreateCmd(
                id = CmdId(12),
                extId = ExtensionId(1234),
                groupId = testGroup.id,
                name = ExtensionName("TestExtension"),
                code = ExtensionCode(
                    id = CodeId(3),
                    version = CodeVersion(3)
                )
            )
        )

        with(getExtensionList()) {
            assertThat(extensions, hasSize(1))
            with(extensions.first()) {
                assertThat(id, equalTo(ExtensionId(1234)))
                assertThat(name, equalTo(ExtensionName("TestExtension")))
            }
        }
    }

    @Test
    fun `Limit extensions`() {
        awaitCompleted(
            IntRange(0, 20).map {
                createExtension(
                    ApiCreateExtensionReq(
                        name = ExtensionName("ex-$it"),
                        codeId = CodeId("$it"),
                        codeVersion = CodeVersion(1)
                    )
                )
            }
        )

        val listResponse = httpTemplate.get("/v1/extensions")
            .parameter("group_ids", testGroup.id)
            .parameter("limit", 12)
            .execute(ApiExtensionList::class)

        assertThat(listResponse.extensions, hasSize(12))
    }


    @Test
    fun `Skip and limit extensions`() {
        val requests = awaitCompleted(IntRange(0, 99).map {
            createExtension(
                ApiCreateExtensionReq(
                    name = ExtensionName("ex-$it"),
                    codeId = CodeId("$it"),
                    codeVersion = CodeVersion(1)
                )
            )
        }
        )

        val fortyNinth = requests.toList()[49]

        val listResponse = httpTemplate.get("/v1/extensions")
            .parameter("group_ids", testGroup.id)
            .parameter("after_id", fortyNinth.id)
            .parameter("limit", 1)
            .execute(ApiExtensionList::class)

        assertThat(listResponse.extensions, hasSize(1))

        val ext = listResponse.extensions.first()
        assertThat(ext.name, equalTo(ExtensionName("ex-48")))
    }

}