package io.hamal.api.http.extension

import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.ExtensionName
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiExtensionCreateReq
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class ExtensionGetControllerTest : ExtensionBaseControllerTest() {

    @Test
    fun `Get extension`() {
        val extId = awaitCompleted(
            createExtension(
                ApiExtensionCreateReq(
                    name = ExtensionName("TestExtension"),
                    code = CodeValue("40 + 2")
                )
            )
        ).id

        val ext = getExtension(extId)
        assertThat(ext.name, equalTo(ExtensionName("TestExtension")))
        assertThat(codeQueryRepository.get(ext.code.id).value, equalTo(CodeValue("40 + 2")))
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