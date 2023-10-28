package io.hamal.api.http.func

import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.lib.domain.vo.FuncInputs
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiFunc
import io.hamal.lib.sdk.api.ApiFuncCreateReq
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class FuncGetControllerTest : FuncBaseControllerTest() {
    @Test
    fun `Func does not exists`() {
        val getFuncResponse = httpTemplate.get("/v1/funcs/33333333").execute()
        assertThat(getFuncResponse.statusCode, equalTo(HttpStatusCode.NotFound))
        require(getFuncResponse is ErrorHttpResponse) { "request was successful" }

        val error = getFuncResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Func not found"))
    }

    @Test
    fun `Get func`() {
        val funcId = awaitCompleted(
            createFunc(
                ApiFuncCreateReq(
                    name = FuncName("func-one"),
                    inputs = FuncInputs(MapType(mutableMapOf("hamal" to StringType("rockz")))),
                    code = CodeValue("1+1")
                )
            )
        ).id

        val getFuncResponse = httpTemplate.get("/v1/funcs/{funcId}").path("funcId", funcId).execute()
        assertThat(getFuncResponse.statusCode, equalTo(HttpStatusCode.Ok))
        require(getFuncResponse is SuccessHttpResponse) { "request was not successful" }

        with(getFuncResponse.result(ApiFunc::class)) {
            assertThat(id, equalTo(funcId))
            assertThat(name, equalTo(FuncName("func-one")))
            assertThat(inputs, equalTo(FuncInputs(MapType(mutableMapOf("hamal" to StringType("rockz"))))))

            assertThat(code.version, equalTo(CodeVersion(1)))
            assertThat(code.value, equalTo(CodeValue("1+1")))
        }
    }
}