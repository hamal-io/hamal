package io.hamal.api.web.func

import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncInputs
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.sdk.hub.HubCreateFuncReq
import io.hamal.lib.sdk.hub.HubError
import io.hamal.lib.sdk.hub.HubInvokeFuncReq
import io.hamal.lib.sdk.hub.HubSubmittedReqWithId
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class InvokeFuncControllerTest : BaseFuncControllerTest() {

    @Test
    fun `Invokes func`() {
        val createResponse = awaitCompleted(
            createFunc(
                HubCreateFuncReq(
                    name = FuncName("test"),
                    namespaceId = null,
                    inputs = FuncInputs(),
                    code = CodeType("")
                )
            )
        )

        val invocationResponse = httpTemplate.post("/v1/funcs/{funcId}/exec")
            .path("funcId", createResponse.id)
            .body(
                HubInvokeFuncReq(
                    correlationId = CorrelationId("some-correlation-id"),
                    inputs = InvocationInputs()
                )
            ).execute()

        assertThat(invocationResponse.statusCode, equalTo(Accepted))
        require(invocationResponse is SuccessHttpResponse) { "request was not successful" }

        val result = invocationResponse.result(HubSubmittedReqWithId::class)
        awaitCompleted(result.reqId)
    }

    @Test
    fun `Invokes func without providing correlation id`() {
        val createResponse = awaitCompleted(
            createFunc(
                HubCreateFuncReq(
                    name = FuncName("test"),
                    namespaceId = null,
                    inputs = FuncInputs(),
                    code = CodeType("")
                )
            )
        )

        val invocationResponse = httpTemplate.post("/v1/funcs/{funcId}/exec")
            .path("funcId", createResponse.id)
            .body(
                HubInvokeFuncReq(
                    inputs = InvocationInputs(),
                    correlationId = null
                )
            ).execute()

        assertThat(invocationResponse.statusCode, equalTo(Accepted))
        require(invocationResponse is SuccessHttpResponse) { "request was not successful" }

        val result = invocationResponse.result(HubSubmittedReqWithId::class)
        awaitCompleted(result.reqId)
    }

    @Test
    fun `Tries to invoke func which does not exist`() {
        val invocationResponse = httpTemplate.post("/v1/funcs/1234/exec")
            .body(
                HubInvokeFuncReq(
                    correlationId = CorrelationId("__default__"),
                    inputs = InvocationInputs()
                )
            ).execute()

        assertThat(invocationResponse.statusCode, equalTo(NotFound))
        require(invocationResponse is ErrorHttpResponse) { "request was successful" }

        val error = invocationResponse.error(HubError::class)
        assertThat(error.message, equalTo("Func not found"))

        verifyNoRequests()
    }
}