package io.hamal.admin.web.func

import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.admin.AdminCreateFuncReq
import io.hamal.lib.sdk.admin.AdminError
import io.hamal.lib.sdk.admin.AdminFunc
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class GetFuncControllerTest : BaseFuncControllerTest() {
    @Test
    fun `Func does not exists`() {
        val getFuncResponse = httpTemplate.get("/v1/funcs/33333333").execute()
        assertThat(getFuncResponse.statusCode, equalTo(HttpStatusCode.NotFound))
        require(getFuncResponse is ErrorHttpResponse) { "request was successful" }

        val error = getFuncResponse.error(AdminError::class)
        assertThat(error.message, equalTo("Func not found"))
    }

    @Test
    fun `Get func`() {
        val funcId = awaitCompleted(
            createFunc(
                AdminCreateFuncReq(
                    name = FuncName("func-one"),
                    namespaceId = null,
                    inputs = FuncInputs(MapType(mutableMapOf("hamal" to StringType("rockz")))),
                    code = CodeValue("1+1")
                )
            )
        ).id(::FuncId)

        val getFuncResponse = httpTemplate.get("/v1/funcs/{funcId}").path("funcId", funcId).execute()
        assertThat(getFuncResponse.statusCode, equalTo(HttpStatusCode.Ok))
        require(getFuncResponse is SuccessHttpResponse) { "request was not successful" }

        with(getFuncResponse.result(AdminFunc::class)) {
            assertThat(id, equalTo(funcId))
            assertThat(name, equalTo(FuncName("func-one")))
            assertThat(inputs, equalTo(FuncInputs(MapType(mutableMapOf("hamal" to StringType("rockz"))))))

            assertThat(code.version, equalTo(CodeVersion(1)))
            assertThat(code.value, equalTo(CodeValue("1+1")))
        }
    }
}