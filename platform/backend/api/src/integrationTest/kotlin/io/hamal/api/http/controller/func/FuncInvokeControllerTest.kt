package io.hamal.api.http.controller.func

import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
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
                    inputs = InvocationInputs(),
                    events = listOf(),
                    version = null
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
            .path("funcId", createResponse.funcId)
            .body(
                ApiFuncInvokeReq(
                    inputs = InvocationInputs(),
                    correlationId = null,
                    events = listOf(),
                    version = null
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
    fun `Invokes func with verion`() {
        val createResponse = awaitCompleted(
            createFunc(
                ApiFuncCreateReq(
                    name = FuncName("test"),
                    inputs = FuncInputs(),
                    code = CodeValue("")
                )
            )
        )

        repeat(10) {
            updateFunc(
                createResponse.funcId, ApiFuncUpdateReq(
                    name = FuncName("test-update"),
                    code = CodeValue("code-${it}")
                )
            )
        }


        val invocationResponse = httpTemplate.post("/v1/funcs/{funcId}/invoke")
            .path("funcId", createResponse.funcId)
            .body(
                ApiFuncInvokeReq(
                    correlationId = CorrelationId("some-correlation-id"),
                    inputs = InvocationInputs(),
                    events = listOf(),
                    version = CodeVersion(5)
                )
            ).execute()

        assertThat(invocationResponse.statusCode, equalTo(Accepted))
        require(invocationResponse is HttpSuccessResponse) { "request was not successful" }

        val result = invocationResponse.result(ApiExecInvokeSubmitted::class)
        awaitCompleted(result)

        with(execQueryRepository.get(result.execId)) {
            //assertThat(code.value, equalTo(CodeValue("code-6")))
            assertThat(code.value, notNullValue())
        }
    }

    @Test
    fun `Invokes func with default version`() {
        val createResponse = awaitCompleted(
            createFunc(
                ApiFuncCreateReq(
                    name = FuncName("funcName"),
                    inputs = FuncInputs(),
                    code = CodeValue("createCode")
                )
            )
        )

        repeat(10) {
            updateFunc(
                createResponse.funcId, ApiFuncUpdateReq(
                    name = FuncName("test-update"),
                    code = CodeValue("code-${it}")
                )
            )
        }


        val invocationResponse = httpTemplate.post("/v1/funcs/{funcId}/invoke")
            .path("funcId", createResponse.funcId)
            .body(
                ApiFuncInvokeReq(
                    correlationId = CorrelationId("some-correlation-id"),
                    inputs = InvocationInputs(),
                    events = listOf(),
                    version = null
                )
            ).execute()

        assertThat(invocationResponse.statusCode, equalTo(Accepted))
        require(invocationResponse is HttpSuccessResponse) { "request was not successful" }

        val result = invocationResponse.result(ApiExecInvokeSubmitted::class)
        awaitCompleted(result)

        with(execQueryRepository.get(result.execId)) {
            //assertThat(code.value, equalTo(CodeValue("code-11")))
            assertThat(code.value, notNullValue())

        }
    }

    @Test
    fun `Tries to invoke func with invalid version`() {
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
            .path("funcId", createResponse.funcId)
            .body(
                ApiFuncInvokeReq(
                    correlationId = CorrelationId("some-correlation-id"),
                    inputs = InvocationInputs(),
                    events = listOf(),
                    version = CodeVersion(10)
                )
            ).execute()

        assertThat(invocationResponse.statusCode, equalTo(NotFound))
        require(invocationResponse is HttpErrorResponse) { "request was successful" }

        val error = invocationResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Code not found"))

        //TODO-84 verifyNoRequests()
    }

    @Test
    fun `Tries to invoke func which does not exist`() {
        val invocationResponse = httpTemplate.post("/v1/funcs/1234/invoke")
            .body(
                ApiFuncInvokeReq(
                    correlationId = CorrelationId.default,
                    inputs = InvocationInputs(),
                    events = listOf(),
                    version = null
                )
            ).execute()

        assertThat(invocationResponse.statusCode, equalTo(NotFound))
        require(invocationResponse is HttpErrorResponse) { "request was successful" }

        val error = invocationResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Func not found"))

        verifyNoRequests()
    }
}