package io.hamal.bridge.http.controller.code


import io.hamal.bridge.http.controller.BaseControllerTest
import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.lib.http.HttpStatusCode.Ok
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.sdk.bridge.BridgeCode
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class CodeBaseControllerTest : BaseControllerTest() {
    fun getCode(codeId: CodeId): BridgeCode {
        val getCodeResponse = httpTemplate.get("/b1/code/{id}")
            .path("id", codeId)
            .execute()

        assertThat(getCodeResponse.statusCode, equalTo(Ok))
        require(getCodeResponse is HttpSuccessResponse) { "request was not successful" }
        return getCodeResponse.result(BridgeCode::class)
    }

    fun getCode(codeId: CodeId, codeVersion: CodeVersion): BridgeCode {
        val getCodeResponse = httpTemplate.get("/b1/code/{id}")
            .path("id", codeId)
            .parameter("version", codeVersion.intValue)
            .execute()

        assertThat(getCodeResponse.statusCode, equalTo(Ok))
        require(getCodeResponse is HttpSuccessResponse) { "request was not successful" }
        return getCodeResponse.result(BridgeCode::class)
    }
}