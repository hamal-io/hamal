package io.hamal.api.http.extension

import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.lib.domain.vo.ExtensionName
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.sdk.api.ApiCreateExtensionReq
import io.hamal.lib.sdk.api.ApiError
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class ExtensionGetControllerTest : ExtensionBaseControllerTest() {

    @Test
    fun `Get extension`() {
        val extId = awaitCompleted(
            createExtension(
                ApiCreateExtensionReq(
                    name = ExtensionName("TestExtension"),
                    codeId = CodeId(1),
                    codeVersion = CodeVersion(1)
                )
            )
        ).id(::ExtensionId)

        with(getExtension(extId)) {
            assertThat(name, equalTo(ExtensionName("TestExtension")))
            assertThat(code.id, equalTo(CodeId(1)))
            assertThat(code.version, equalTo(CodeVersion(1)))
        }
    }

    @Test
    fun `Tries to get extension that does not exist`() {
        val getResponse = httpTemplate.get("/v1/extensions/33333333").execute()
        assertThat(getResponse.statusCode, equalTo(HttpStatusCode.NotFound))
        require(getResponse is ErrorHttpResponse) { "request was successful" }

        val error = getResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Extension not found"))
    }
}