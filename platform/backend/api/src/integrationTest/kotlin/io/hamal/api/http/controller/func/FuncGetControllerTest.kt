package io.hamal.api.http.controller.func

import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.domain._enum.CodeType
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.lib.domain.vo.FuncInputs
import io.hamal.lib.domain.vo.FuncName.Companion.FuncName
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiFunc
import io.hamal.lib.sdk.api.ApiFuncCreateRequest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class FuncGetControllerTest : FuncBaseControllerTest() {
    @Test
    fun `Func does not exists`() {
        val getFuncResponse = httpTemplate.get("/v1/funcs/33333333").execute()
        assertThat(getFuncResponse.statusCode, equalTo(HttpStatusCode.NotFound))
        require(getFuncResponse is HttpErrorResponse) { "request was successful" }

        val error = getFuncResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Func not found"))
    }

    @Test
    fun `Get func`() {
        val funcId = awaitCompleted(
            createFunc(
                ApiFuncCreateRequest(
                    name = FuncName("func-one"),
                    inputs = FuncInputs(HotObject.builder().set("hamal", "rocks").build()),
                    code = CodeValue("1+1"),
                    codeType = CodeType.Lua54
                )
            )
        ).id

        val getFuncResponse = httpTemplate.get("/v1/funcs/{funcId}").path("funcId", funcId).execute()
        assertThat(getFuncResponse.statusCode, equalTo(HttpStatusCode.Ok))
        require(getFuncResponse is HttpSuccessResponse) { "request was not successful" }

        with(getFuncResponse.result(ApiFunc::class)) {
            assertThat(id, equalTo(funcId))
            assertThat(name, equalTo(FuncName("func-one")))
            assertThat(inputs, equalTo(FuncInputs(HotObject.builder().set("hamal", "rocks").build())))

            assertThat(code.version, equalTo(CodeVersion(1)))
            assertThat(code.value, equalTo(CodeValue("1+1")))

            assertThat(deployment.version, equalTo(CodeVersion(1)))
            assertThat(deployment.value, equalTo(CodeValue("1+1")))
        }
    }
}