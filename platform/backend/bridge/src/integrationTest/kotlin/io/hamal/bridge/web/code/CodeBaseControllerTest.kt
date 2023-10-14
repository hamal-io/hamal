package io.hamal.bridge.web.code


import io.hamal.bridge.web.BaseControllerTest
import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.lib.http.HttpStatusCode.*
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.sdk.api.ApiCode
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*

internal sealed class CodeBaseControllerTest : BaseControllerTest() {
    fun getCode(codeId: CodeId): ApiCode {
        val getCodeResponse = httpTemplate.get("/b1/code/{id}")
            .path("id", codeId)
            .execute()

        assertThat(getCodeResponse.statusCode, equalTo(Ok))
        require(getCodeResponse is SuccessHttpResponse) { "request was not successful" }
        return getCodeResponse.result(ApiCode::class)
    }

    fun getCode(codeId: CodeId, codeVersion: CodeVersion): ApiCode {
        val getCodeResponse = httpTemplate.get("/b1/code/{id}")
            .path("id", codeId)
            .parameter("version", codeVersion.value)
            .execute()

        assertThat(getCodeResponse.statusCode, equalTo(Ok))
        require(getCodeResponse is SuccessHttpResponse) { "request was not successful" }
        return getCodeResponse.result(ApiCode::class)
    }
}