package io.hamal.backend.instance.web.func

import io.hamal.lib.domain.HamalError
import io.hamal.lib.domain.req.CreateFuncReq
import io.hamal.lib.domain.req.InvokeOneshotReq
import io.hamal.lib.domain.req.SubmittedInvokeOneshotReq
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class InvokeOneshotRouteIT : BaseFuncRouteIT() {

    @Test
    fun `Invokes func`() {
        val createResponse = createFunc(
            CreateFuncReq(
                name = FuncName("test"),
                inputs = FuncInputs(),
                secrets = FuncSecrets(),
                code = Code("")
            )
        )
        val invocationResponse = httpTemplate.post("/v1/funcs/${createResponse.funcId.value.value}/exec")
            .body(
                InvokeOneshotReq(
                    correlationId = CorrelationId("__default__"),
                    inputs = InvocationInputs(),
                    secrets = InvocationSecrets()
                )
            ).execute()

        assertThat(invocationResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(invocationResponse is SuccessHttpResponse) { "request was not successful" }

        val result = invocationResponse.result(SubmittedInvokeOneshotReq::class)
        assertThat(result.funcId, equalTo(createResponse.funcId))
        assertThat(result.inputs, equalTo(InvocationInputs()))
        assertThat(result.secrets, equalTo(InvocationSecrets()))
        assertThat(result.correlationId, equalTo(CorrelationId("__default__")))

        awaitReqCompleted(result.id)
    }


    @Test
    fun `Tries to invoke func which does not exist`() {
        val invocationResponse = httpTemplate.post("/v1/funcs/1234/exec")
            .body(
                InvokeOneshotReq(
                    correlationId = CorrelationId("__default__"),
                    inputs = InvocationInputs(),
                    secrets = InvocationSecrets()
                )
            ).execute()

        assertThat(invocationResponse.statusCode, equalTo(HttpStatusCode.NotFound))
        require(invocationResponse is ErrorHttpResponse) { "request was successful" }

        val error = invocationResponse.error(HamalError::class)
        assertThat(error.message, equalTo("Func not found"))

        verifyNoRequests()
    }
}