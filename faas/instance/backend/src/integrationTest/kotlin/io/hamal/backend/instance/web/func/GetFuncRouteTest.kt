package io.hamal.backend.instance.web.func

import io.hamal.lib.sdk.domain.ApiError
import io.hamal.lib.domain.req.CreateFuncReq
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.FuncInputs
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.kua.type.TableType
import io.hamal.lib.sdk.domain.ApiFunc
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class GetFuncRouteTest : BaseFuncRouteTest() {
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
                CreateFuncReq(
                    name = FuncName("func-one"),
                    inputs = FuncInputs(TableType("hamal" to StringType("rockz"))),
                    code = CodeType("1+1")
                )
            )
        ).id(::FuncId)

        val getFuncResponse = httpTemplate.get("/v1/funcs/${funcId.value}").execute()
        assertThat(getFuncResponse.statusCode, equalTo(HttpStatusCode.Ok))
        require(getFuncResponse is SuccessHttpResponse) { "request was not successful" }

        with(getFuncResponse.result(ApiFunc::class)) {
            assertThat(id, equalTo(funcId))
            assertThat(name, equalTo(FuncName("func-one")))
            assertThat(inputs, equalTo(FuncInputs(TableType("hamal" to StringType("rockz")))))
            assertThat(code, equalTo(CodeType("1+1")))
        }
    }
}