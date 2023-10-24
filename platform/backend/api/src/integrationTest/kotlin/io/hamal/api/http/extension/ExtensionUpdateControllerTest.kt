package io.hamal.api.http.extension

import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.lib.domain.vo.ExtensionName
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiCreateExtensionReq
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiUpdateExtensionReq
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class ExtensionUpdateControllerTest : ExtensionBaseControllerTest() {

    @Test
    fun `Updates extension`() {
        val extId = awaitCompleted(
            createExtension(
                ApiCreateExtensionReq(
                    name = ExtensionName("TestExtension"),
                    codeId = CodeId(1),
                    codeVersion = CodeVersion(1)
                )
            )
        ).id(::ExtensionId)

        awaitCompleted(
            updateExtension(
                extId,
                ApiUpdateExtensionReq(
                    name = ExtensionName("UpdateExtension"),
                    codeId = CodeId(2),
                    codeVersion = CodeVersion(2)
                )
            )
        )

        with(extensionQueryRepository.get(extId)) {
            assertThat(name, equalTo(ExtensionName("UpdateExtension")))
            assertThat(code.id, equalTo(CodeId(2)))
            assertThat(code.version, equalTo(CodeVersion(2)))
        }
    }

    @Test
    fun `Updates extension without updating values`() {
        val extId = awaitCompleted(
            createExtension(
                ApiCreateExtensionReq(
                    name = ExtensionName("TestExtension"),
                    codeId = CodeId(1),
                    codeVersion = CodeVersion(1)
                )
            )
        ).id(::ExtensionId)

        awaitCompleted(
            updateExtension(
                extId,
                ApiUpdateExtensionReq(
                    name = null,
                    codeId = null,
                    codeVersion = null
                )
            )
        )

        with(extensionQueryRepository.get(extId)) {
            assertThat(name, equalTo(ExtensionName("TestExtension")))
            assertThat(code.id, equalTo(CodeId(1)))
            assertThat(code.version, equalTo(CodeVersion(1)))
        }
    }

    @Test
    fun `Tries to update extension that does not exist`() {
        val updateResponse = httpTemplate.patch("/v1/extensions/123456/update")
            .body(
                ApiUpdateExtensionReq(
                    name = ExtensionName("UpdateExtension"),
                    codeId = CodeId(2),
                    codeVersion = CodeVersion(2)
                )
            )
            .execute()

        assertThat(updateResponse.statusCode, equalTo(HttpStatusCode.NotFound))
        require(updateResponse is ErrorHttpResponse) { "request was successful" }

        val error = updateResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Extension not found"))
    }
}
