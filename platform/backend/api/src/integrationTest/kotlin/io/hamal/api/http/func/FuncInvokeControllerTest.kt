package io.hamal.api.http.func

import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiCreateFuncReq
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiInvokeFuncReq
import io.hamal.lib.sdk.api.ApiSubmittedReqWithId
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class FuncInvokeControllerTest : FuncBaseControllerTest() {

    @Test
    fun `Invokes func with correlation id`() {
        val createResponse = awaitCompleted(
            createFunc(
                req = ApiCreateFuncReq(
                    name = FuncName("test"),
                    inputs = FuncInputs(),
                    code = CodeValue("x = 10")
                )
            )
        )

        val invocationResponse = httpTemplate.post("/v1/funcs/{funcId}/invoke")
            .path("funcId", createResponse.id)
            .body(
                ApiInvokeFuncReq(
                    correlationId = CorrelationId("some-correlation-id"),
                    inputs = InvocationInputs()
                )
            ).execute()

        assertThat(invocationResponse.statusCode, equalTo(Accepted))
        require(invocationResponse is SuccessHttpResponse) { "request was not successful" }

        val result = invocationResponse.result(ApiSubmittedReqWithId::class)
        awaitCompleted(result.reqId)

        with(execQueryRepository.get(result.id(::ExecId))) {
            assertThat(
                correlation, equalTo(
                    Correlation(
                        correlationId = CorrelationId("some-correlation-id"),
                        funcId = createResponse.id(::FuncId)
                    )
                )
            )
        }
    }

    @Test
    fun `Invokes func without correlation id`() {
        val createResponse = awaitCompleted(
            createFunc(
                ApiCreateFuncReq(
                    name = FuncName("test"),
                    inputs = FuncInputs(),
                    code = CodeValue("")
                )
            )
        )

        val invocationResponse = httpTemplate.post("/v1/funcs/{funcId}/invoke")
            .path("funcId", createResponse.id)
            .body(
                ApiInvokeFuncReq(
                    inputs = InvocationInputs(),
                    correlationId = null
                )
            ).execute()

        assertThat(invocationResponse.statusCode, equalTo(Accepted))
        require(invocationResponse is SuccessHttpResponse) { "request was not successful" }

        val result = invocationResponse.result(ApiSubmittedReqWithId::class)
        awaitCompleted(result.reqId)

        with(execQueryRepository.get(result.id(::ExecId))) {
            assertThat(
                correlation, equalTo(
                    Correlation(
                        correlationId = CorrelationId.default,
                        funcId = createResponse.id(::FuncId)
                    )
                )
            )
        }
    }

    @Test
    fun `Tries to invoke func which does not exist`() {
        val invocationResponse = httpTemplate.post("/v1/funcs/1234/invoke")
            .body(
                ApiInvokeFuncReq(
                    correlationId = CorrelationId.default,
                    inputs = InvocationInputs()
                )
            ).execute()

        assertThat(invocationResponse.statusCode, equalTo(NotFound))
        require(invocationResponse is ErrorHttpResponse) { "request was successful" }

        val error = invocationResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Func not found"))

        verifyNoRequests()
    }
}