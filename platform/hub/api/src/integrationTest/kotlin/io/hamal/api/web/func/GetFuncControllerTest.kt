package io.hamal.api.web.func

import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.FuncInputs
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.hub.HubCreateFuncReq
import io.hamal.lib.sdk.hub.HubError
import io.hamal.lib.sdk.hub.HubFunc
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class GetFuncControllerTest : BaseFuncControllerTest() {
    @Test
    fun `Func does not exists`() {
        val getFuncResponse = httpTemplate.get("/v1/funcs/33333333").execute()
        assertThat(getFuncResponse.statusCode, equalTo(HttpStatusCode.NotFound))
        require(getFuncResponse is ErrorHttpResponse) { "request was successful" }

        val error = getFuncResponse.error(HubError::class)
        assertThat(error.message, equalTo("Func not found"))
    }

    @Test
    fun `Get func`() {
        val funcId = awaitCompleted(
            createFunc(
                HubCreateFuncReq(
                    name = FuncName("func-one"),
                    namespaceId = null,
                    inputs = FuncInputs(MapType(mutableMapOf("hamal" to StringType("rockz")))),
                    code = CodeType("1+1")
                )
            )
        ).id(::FuncId)

        val getFuncResponse = httpTemplate.get("/v1/funcs/{funcId}").path("funcId", funcId).execute()
        assertThat(getFuncResponse.statusCode, equalTo(HttpStatusCode.Ok))
        require(getFuncResponse is SuccessHttpResponse) { "request was not successful" }

        with(getFuncResponse.result(HubFunc::class)) {
            assertThat(id, equalTo(funcId))
            assertThat(name, equalTo(FuncName("func-one")))
            assertThat(inputs, equalTo(FuncInputs(MapType(mutableMapOf("hamal" to StringType("rockz"))))))
            assertThat(code, equalTo(CodeType("1+1")))
        }
    }
}