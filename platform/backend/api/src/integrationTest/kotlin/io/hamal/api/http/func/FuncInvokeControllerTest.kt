package io.hamal.api.http.func

import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiExecInvokeSubmitted
import io.hamal.lib.sdk.api.ApiFuncCreateReq
import io.hamal.lib.sdk.api.ApiFuncInvokeReq
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class FuncInvokeControllerTest : FuncBaseControllerTest() {

    @Test
    fun `Invokes func with correlation id`() {
        val createResponse = awaitCompleted(
            createFunc(
                req = ApiFuncCreateReq(
                    name = FuncName("test"),
                    inputs = FuncInputs(),
                    code = CodeValue("x = 10")
                )
            )
        )

        val invocationResponse = httpTemplate.post("/v1/funcs/{funcId}/invoke")
            .path("funcId", createResponse.funcId)
            .body(
                ApiFuncInvokeReq(
                    correlationId = CorrelationId("some-correlation-id"),
                    inputs = InvocationInputs()
                )
            ).execute()

        assertThat(invocationResponse.statusCode, equalTo(Accepted))
        require(invocationResponse is HttpSuccessResponse) { "request was not successful" }

        val result = invocationResponse.result(ApiExecInvokeSubmitted::class)
        awaitCompleted(result.id)

        with(execQueryRepository.get(result.execId)) {
            assertThat(
                correlation, equalTo(
                    Correlation(
                        correlationId = CorrelationId("some-correlation-id"),
                        funcId = createResponse.funcId
                    )
                )
            )
        }
    }

    @Test
    fun `Invokes func without correlation id`() {
        val createResponse = awaitCompleted(
            createFunc(
                ApiFuncCreateReq(
                    name = FuncName("test"),
                    inputs = FuncInputs(),
                    code = CodeValue("")
                )
            )
        )

        val invocationResponse = httpTemplate.post("/v1/funcs/{funcId}/invoke")
            .path("funcId", createResponse.id)
            .body(
                ApiFuncInvokeReq(
                    inputs = InvocationInputs(),
                    correlationId = null
                )
            ).execute()

        assertThat(invocationResponse.statusCode, equalTo(Accepted))
        require(invocationResponse is HttpSuccessResponse) { "request was not successful" }

        val result = invocationResponse.result(ApiExecInvokeSubmitted::class)
        awaitCompleted(result)

        with(execQueryRepository.get(result.execId)) {
            assertThat(
                correlation, equalTo(
                    Correlation(
                        correlationId = CorrelationId.default,
                        funcId = createResponse.funcId
                    )
                )
            )
        }
    }

    @Test
    fun `Tries to invoke func which does not exist`() {
        val invocationResponse = httpTemplate.post("/v1/funcs/1234/invoke")
            .body(
                ApiFuncInvokeReq(
                    correlationId = CorrelationId.default,
                    inputs = InvocationInputs()
                )
            ).execute()

        assertThat(invocationResponse.statusCode, equalTo(NotFound))
        require(invocationResponse is HttpErrorResponse) { "request was successful" }

        val error = invocationResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Func not found"))

        verifyNoRequests()
    }
}