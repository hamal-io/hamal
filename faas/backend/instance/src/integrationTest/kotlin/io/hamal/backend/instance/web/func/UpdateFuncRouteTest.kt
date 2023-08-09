package io.hamal.backend.instance.web.func

import io.hamal.lib.domain.HamalError
import io.hamal.lib.domain.req.CreateFuncReq
import io.hamal.lib.domain.req.UpdateFuncReq
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.FuncInputs
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.kua.value.CodeValue
import io.hamal.lib.kua.value.StringValue
import io.hamal.lib.kua.value.TableValue
import io.hamal.lib.sdk.domain.ApiSubmittedReqWithDomainId
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class UpdateFuncRouteTest : BaseFuncRouteTest() {

    @Test
    fun `Tries to update func which does not exists`() {
        val getFuncResponse = httpTemplate.put("/v1/funcs/33333333")
            .body(
                UpdateFuncReq(
                    name = FuncName("update"),
                    inputs = FuncInputs(),
                    code = CodeValue("")
                )
            )
            .execute()

        assertThat(getFuncResponse.statusCode, equalTo(HttpStatusCode.NotFound))
        require(getFuncResponse is ErrorHttpResponse) { "request was successful" }

        val error = getFuncResponse.error(HamalError::class)
        assertThat(error.message, equalTo("Func not found"))
    }

    @Test
    fun `Updates func`() {
        val func = awaitCompleted(
            createFunc(
                CreateFuncReq(
                    name = FuncName("createdName"),
                    inputs = FuncInputs(TableValue("hamal" to StringValue("createdInputs"))),
                    code = CodeValue("createdCode")
                )
            )
        )

        val updateFuncResponse = httpTemplate.put("/v1/funcs/${func.id.value}")
            .body(
                UpdateFuncReq(
                    name = FuncName("updatedName"),
                    inputs = FuncInputs(TableValue("hamal" to StringValue("updatedInputs"))),
                    code = CodeValue("updatedCode")
                )
            )
            .execute()
        assertThat(updateFuncResponse.statusCode, equalTo(Accepted))
        require(updateFuncResponse is SuccessHttpResponse) { "request was not successful" }

        val funcId = updateFuncResponse.result(ApiSubmittedReqWithDomainId::class).id(::FuncId)

        with(getFunc(funcId)) {
            assertThat(id, equalTo(funcId))
            assertThat(name, equalTo(FuncName("updatedName")))
            assertThat(inputs, equalTo(FuncInputs(TableValue("hamal" to StringValue("updatedInputs")))))
            assertThat(code, equalTo(CodeValue("updatedCode")))
        }
    }
}