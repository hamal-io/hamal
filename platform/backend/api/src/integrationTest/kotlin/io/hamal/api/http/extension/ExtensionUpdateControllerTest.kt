package io.hamal.api.http.extension

import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.ExtensionName
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiExtensionCreateReq
import io.hamal.lib.sdk.api.ApiExtensionUpdateReq
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class ExtensionUpdateControllerTest : ExtensionBaseControllerTest() {

    @Test
    fun `Updates extension`() {
        val extId = awaitCompleted(
            createExtension(
                ApiExtensionCreateReq(
                    name = ExtensionName("TestExtension"),
                    code = CodeValue("x='hamal")
                )
            )
        ).id

        awaitCompleted(
            updateExtension(
                extId,
                ApiExtensionUpdateReq(
                    name = ExtensionName("UpdateExtension"),
                    code = CodeValue("x='hamal")
                )
            )
        )

        val ext = extensionQueryRepository.get(extId)
        assertThat(ext.name, equalTo(ExtensionName("UpdateExtension")))
        assertThat(codeQueryRepository.get(ext.code.id).value, equalTo(CodeValue("x='hamal")))
    }


    @Test
    fun `Updates extension without updating values`() {
        val extId = awaitCompleted(
            createExtension(
                ApiExtensionCreateReq(
                    name = ExtensionName("TestExtension"),
                    code = CodeValue("x='hamal")
                )
            )
        ).id

        awaitCompleted(
            updateExtension(
                extId,
                ApiExtensionUpdateReq(
                    name = null,
                    code = null
                )
            )
        )

        val ext = extensionQueryRepository.get(extId)
        assertThat(ext.name, equalTo(ExtensionName("TestExtension")))
    }

    @Test
    fun `Tries to update extension that does not exist`() {
        val updateResponse = httpTemplate.patch("/v1/extensions/123456/update")
            .body(
                ApiExtensionUpdateReq(
                    name = ExtensionName("UpdateExtension"),
                    code = CodeValue("x='hamal")
                )
            )
            .execute()

        assertThat(updateResponse.statusCode, equalTo(HttpStatusCode.NotFound))
        require(updateResponse is ErrorHttpResponse) { "request was successful" }

        val error = updateResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Extension not found"))
    }
}
