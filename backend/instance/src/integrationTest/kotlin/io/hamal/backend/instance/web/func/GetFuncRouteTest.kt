package io.hamal.backend.instance.web.func

import io.hamal.lib.domain.Func
import io.hamal.lib.domain.HamalError
import io.hamal.lib.domain.req.CreateFuncReq
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.FuncInputs
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class GetFuncRouteTest : BaseFuncRouteTest() {
    @Test
    fun `Func does not exists`() {
        val getFuncResponse = httpTemplate.get("/v1/funcs/33333333").execute()
        assertThat(getFuncResponse.statusCode, equalTo(HttpStatusCode.NotFound))
        require(getFuncResponse is ErrorHttpResponse) { "request was successful" }

        val error = getFuncResponse.error(HamalError::class)
        assertThat(error.message, equalTo("Func not found"))
    }

    @Test
    fun `Get func`() {
        val result = awaitCompleted(
            createFunc(
                CreateFuncReq(
                    name = FuncName("func-one"),
                    inputs = FuncInputs(),
                    code = Code("1+1")
                )
            )
        )

        val getFuncResponse = httpTemplate.get("/v1/funcs/${result.funcId.value.value}").execute()
        assertThat(getFuncResponse.statusCode, equalTo(HttpStatusCode.Ok))
        require(getFuncResponse is SuccessHttpResponse) { "request was not successful" }

        with(getFuncResponse.result(Func::class)) {
            assertThat(id, equalTo(result.funcId))
            assertThat(name, equalTo(FuncName("func-one")))
            assertThat(inputs, equalTo(FuncInputs()))
            assertThat(code, equalTo(Code("1+1")))
        }
    }
}