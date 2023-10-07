package io.hamal.api.web.func

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

internal class InvokeFuncControllerTest : BaseFuncControllerTest() {

    @Test
    fun `Invokes func`() {
        val createResponse = awaitCompleted(
            createFunc(
                ApiCreateFuncReq(
                    name = FuncName("test"),
                    namespaceId = null,
                    inputs = FuncInputs(),
                    code = CodeValue("")
                )
            )
        )

        val invocationResponse = httpTemplate.post("/v1/funcs/{funcId}/exec")
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
    }

    @Test
    fun `Invokes func without providing correlation id`() {
        val createResponse = awaitCompleted(
            createFunc(
                ApiCreateFuncReq(
                    name = FuncName("test"),
                    namespaceId = null,
                    inputs = FuncInputs(),
                    code = CodeValue("")
                )
            )
        )

        val invocationResponse = httpTemplate.post("/v1/funcs/{funcId}/exec")
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
    }

    @Test
    fun `Tries to invoke func which does not exist`() {
        val invocationResponse = httpTemplate.post("/v1/funcs/1234/exec")
            .body(
                ApiInvokeFuncReq(
                    correlationId = CorrelationId("__default__"),
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