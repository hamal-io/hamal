package io.hamal.api.web.code

import io.hamal.api.web.BaseControllerTest
import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.http.HttpStatusCode.*
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiCode
import io.hamal.lib.sdk.api.ApiCreateFuncReq
import io.hamal.lib.sdk.api.ApiFunc
import io.hamal.lib.sdk.api.ApiSubmittedReqWithId
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*

internal sealed class CodeBaseControllerTest : BaseControllerTest() {
    fun getCode(codeId: CodeId): ApiCode {
        val getCodeResponse = httpTemplate.get("/v1/code/{codeId}")
            .path("codeId", codeId)
            .execute()

        assertThat(getCodeResponse.statusCode, equalTo(Ok))
        require(getCodeResponse is SuccessHttpResponse) { "request was not successful" }
        return getCodeResponse.result(ApiCode::class)
    }

    fun getCode(codeId: CodeId, codeVersion: CodeVersion): ApiCode {
        val getCodeResponse = httpTemplate.get("/v1/code/{codeId}")
            .path("codeId", codeId)
            .parameter("codeVersion", codeVersion.value)
            .execute()

        assertThat(getCodeResponse.statusCode, equalTo(Ok))
        require(getCodeResponse is SuccessHttpResponse) { "request was not successful" }
        return getCodeResponse.result(ApiCode::class)
    }


    fun getFunc(funcId: FuncId): ApiFunc {
        val getFuncResponse = httpTemplate.get("/v1/funcs/{funcId}")
            .path("funcId", funcId)
            .execute()

        assertThat(getFuncResponse.statusCode, equalTo(Ok))
        require(getFuncResponse is SuccessHttpResponse) { "request was not successful" }
        return getFuncResponse.result(ApiFunc::class)
    }
}