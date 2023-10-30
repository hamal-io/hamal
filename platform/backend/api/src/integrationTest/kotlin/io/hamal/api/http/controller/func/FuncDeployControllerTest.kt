package io.hamal.api.http.controller.func

import io.hamal.api.http.controller.func.FuncBaseControllerTest
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.FuncInputs
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiFuncCreateReq
import io.hamal.lib.sdk.api.ApiFuncCreateSubmitted
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class FuncDeployControllerTest : FuncBaseControllerTest() {

    @Test
    fun `Deploy code version`() {
        val create_req = awaitCompleted(
            createFunc(
                ApiFuncCreateReq(
                    name = FuncName("Func-base"),
                    inputs = FuncInputs(),
                    code = CodeValue("40 + 2")
                )
            )
        )
        TODO()
        /*        val res = httpTemplate.post("/v1/funcs/{funcId}/deploy/{version}")
                    .path("funcId", func.id)
                    .path("version", codeVersion.value.toString())
                    .execute()

                assertThat(res.statusCode, equalTo(HttpStatusCode.Accepted))
                require(res is HttpSuccessResponse) { "request was not successful" }
                return res.result(ApiFuncUpdateSubmitted::class)


                repeat(20) { iter -> }

                with(func) {
                    assertThat(name, equalTo(FuncName("Func-base")))
                    assertThat(code.version, equalTo(CodeVersion(1)))
                    assertThat(code.deployedVersion, equalTo(CodeVersion(20)))
                }*/
    }


    @Test
    fun `Deploy same code version`() {
        val func = awaitCompleted(
            createFunc(
                ApiFuncCreateReq(
                    name = FuncName("Func-base"),
                    inputs = FuncInputs(),
                    code = CodeValue("40 + 2")
                )
            )
        )
    }

    @Test
    fun `Deploy prior code version`() {
        val func = awaitCompleted(
            createFunc(
                ApiFuncCreateReq(
                    name = FuncName("Func-base"),
                    inputs = FuncInputs(),
                    code = CodeValue("40 + 2")
                )
            )
        )
    }

    @Test
    fun `Deploy higher code version`() {
        val func = awaitCompleted(
            createFunc(
                ApiFuncCreateReq(
                    name = FuncName("Func-base"),
                    inputs = FuncInputs(),
                    code = CodeValue("40 + 2")
                )
            )
        )
    }

    @Test
    fun `Tries to deploy to func that does not exist`() {
        val res = httpTemplate.post("/v1/funcs/1234/deploy/25")
            .execute()

        assertThat(res.statusCode, equalTo(HttpStatusCode.NotFound))
        require(res is HttpErrorResponse) { "request was successful" }

        val error = res.error(ApiError::class)
        assertThat(error.message, equalTo("Func not found"))
    }

    @Test
    fun `Tries to deploy code version that does not exists`() {
        val f: ApiFuncCreateSubmitted = createFunc(
            ApiFuncCreateReq(
                name = FuncName("Func-base"),
                inputs = FuncInputs(),
                code = CodeValue("40 + 2")
            )
        )

        val res = httpTemplate.post("/v1/funcs/{funcId}/deploy/25")
            .path("funcId", f.funcId)
            .execute()

        assertThat(res.statusCode, equalTo(HttpStatusCode.NotFound))
        require(res is HttpErrorResponse) { "request was successful" }

        val error = res.error(ApiError::class)
        assertThat(error.message, equalTo("Code not found"))
    }

}