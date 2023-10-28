package io.hamal.api.http.func

import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiFuncCreateReq
import io.hamal.lib.sdk.api.ApiFuncInvokeReq
import io.hamal.lib.sdk.api.ApiSubmittedReqImpl
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

@Suppress("UNCHECKED_CAST")
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
            .path("funcId", createResponse.id)
            .body(
                ApiFuncInvokeReq(
                    correlationId = CorrelationId("some-correlation-id"),
                    inputs = InvocationInputs()
                )
            ).execute()

        assertThat(invocationResponse.statusCode, equalTo(Accepted))
        require(invocationResponse is SuccessHttpResponse) { "request was not successful" }

        val result = invocationResponse.result(ApiSubmittedReqImpl::class) as ApiSubmittedReqImpl<ExecId>
        awaitCompleted(result.reqId)

        with(execQueryRepository.get(result.id)) {
            assertThat(
                correlation, equalTo(
                    Correlation(
                        correlationId = CorrelationId("some-correlation-id"),
                        funcId = createResponse.id
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
        require(invocationResponse is SuccessHttpResponse) { "request was not successful" }

        val result = invocationResponse.result(ApiSubmittedReqImpl::class) as ApiSubmittedReqImpl<ExecId>
        awaitCompleted(result.reqId)

        with(execQueryRepository.get(result.id)) {
            assertThat(
                correlation, equalTo(
                    Correlation(
                        correlationId = CorrelationId.default,
                        funcId = createResponse.id
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
        require(invocationResponse is ErrorHttpResponse) { "request was successful" }

        val error = invocationResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Func not found"))

        verifyNoRequests()
    }
}