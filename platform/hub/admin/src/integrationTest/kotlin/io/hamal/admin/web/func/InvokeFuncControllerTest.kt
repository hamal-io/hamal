package io.hamal.admin.web.func

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
import io.hamal.lib.sdk.admin.AdminCreateFuncReq
import io.hamal.lib.sdk.admin.AdminError
import io.hamal.lib.sdk.admin.AdminInvokeFuncReq
import io.hamal.lib.sdk.admin.AdminSubmittedReqWithId
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class InvokeFuncControllerTest : BaseFuncControllerTest() {

    @Test
    fun `Invokes func`() {
        val createResponse = awaitCompleted(
            createFunc(
                AdminCreateFuncReq(
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
                AdminInvokeFuncReq(
                    correlationId = CorrelationId("some-correlation-id"),
                    inputs = InvocationInputs()
                )
            ).execute()

        assertThat(invocationResponse.statusCode, equalTo(Accepted))
        require(invocationResponse is SuccessHttpResponse) { "request was not successful" }

        val result = invocationResponse.result(AdminSubmittedReqWithId::class)
        awaitCompleted(result.reqId)
    }

    @Test
    fun `Invokes func without providing correlation id`() {
        val createResponse = awaitCompleted(
            createFunc(
                AdminCreateFuncReq(
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
                AdminInvokeFuncReq(
                    inputs = InvocationInputs(),
                    correlationId = null
                )
            ).execute()

        assertThat(invocationResponse.statusCode, equalTo(Accepted))
        require(invocationResponse is SuccessHttpResponse) { "request was not successful" }

        val result = invocationResponse.result(AdminSubmittedReqWithId::class)
        awaitCompleted(result.reqId)
    }

    @Test
    fun `Tries to invoke func which does not exist`() {
        val invocationResponse = httpTemplate.post("/v1/funcs/1234/exec")
            .body(
                AdminInvokeFuncReq(
                    correlationId = CorrelationId("__default__"),
                    inputs = InvocationInputs()
                )
            ).execute()

        assertThat(invocationResponse.statusCode, equalTo(NotFound))
        require(invocationResponse is ErrorHttpResponse) { "request was successful" }

        val error = invocationResponse.error(AdminError::class)
        assertThat(error.message, equalTo("Func not found"))

        verifyNoRequests()
    }
}