package io.hamal.api.http.controller.func

import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain._enum.CodeType
import io.hamal.lib.domain.request.ExecInvokeRequested
import io.hamal.lib.domain.vo.CodeVersion.Companion.CodeVersion
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.CorrelationId.Companion.CorrelationId
import io.hamal.lib.domain.vo.FuncInputs
import io.hamal.lib.domain.vo.FuncName.Companion.FuncName
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class FuncInvokeControllerTest : FuncBaseControllerTest() {

    @Test
    fun `Invokes func with correlation id`() {
        val createResponse = awaitCompleted(
            createFunc(
                req = ApiFuncCreateRequest(
                    name = FuncName("test"),
                    inputs = FuncInputs(),
                    code = ValueCode("x = 10"),
                    codeType = CodeType.Lua54
                )
            )
        )

        val invocationResponse =
            httpTemplate.post("/v1/funcs/{funcId}/invoke").path("funcId", createResponse.id).body(
                ApiFuncInvokeRequest(
                    correlationId = CorrelationId("some-correlation-id"),
                    inputs = InvocationInputs(),

                    )
            ).execute()

        assertThat(invocationResponse.statusCode, equalTo(Accepted))
        require(invocationResponse is HttpSuccessResponse) { "request was not successful" }

        val result = invocationResponse.result(ApiExecInvokeRequested::class)
        awaitCompleted(result)

        with(execQueryRepository.get(result.id)) {
            assertThat(
                correlation, equalTo(
                    Correlation(
                        id = CorrelationId("some-correlation-id"),
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
                ApiFuncCreateRequest(
                    name = FuncName("test"),
                    inputs = FuncInputs(),
                    code = ValueCode(""),
                    codeType = CodeType.Lua54
                )
            )
        )

        val invocationResponse =
            httpTemplate.post("/v1/funcs/{funcId}/invoke").path("funcId", createResponse.id).body(
                ApiFuncInvokeRequest(
                    inputs = InvocationInputs(),
                    correlationId = null,
                )
            ).execute()

        assertThat(invocationResponse.statusCode, equalTo(Accepted))
        require(invocationResponse is HttpSuccessResponse) { "request was not successful" }

        val result = invocationResponse.result(ApiExecInvokeRequested::class)
        awaitCompleted(result)

        with(execQueryRepository.get(result.id)) {
            assertThat(
                correlation, equalTo(
                    Correlation(
                        id = CorrelationId.default, funcId = createResponse.id
                    )
                )
            )
        }
    }

    @Test
    fun `Invokes func with version`() {
        val createResponse = awaitCompleted(
            createFunc(
                ApiFuncCreateRequest(
                    name = FuncName("test"),
                    inputs = FuncInputs(),
                    code = ValueCode(""),
                    codeType = CodeType.Lua54
                )
            )
        )

        repeat(10) {
            awaitCompleted(
                updateFunc(
                    createResponse.id, ApiFuncUpdateRequest(
                        name = FuncName("test-update"),
                        code = ValueCode("code-${it}")
                    )
                )
            )
        }


        val invocationResponse =
            httpTemplate.post("/v1/funcs/{funcId}/invoke").path("funcId", createResponse.id).body(
                ApiFuncInvokeRequest(
                    correlationId = CorrelationId("some-correlation-id"),
                    inputs = InvocationInputs(),
                    version = CodeVersion(5)
                )
            ).execute()

        assertThat(invocationResponse.statusCode, equalTo(Accepted))
        require(invocationResponse is HttpSuccessResponse) { "request was not successful" }

        val result = invocationResponse.result(ApiExecInvokeRequested::class)
        awaitCompleted(result)

        with(execQueryRepository.get(result.id)) {
            assertThat(code.version, equalTo(CodeVersion(5)))
        }
    }

    @Test
    fun `Invokes func with default version`() {
        val createResponse = awaitCompleted(
            createFunc(
                ApiFuncCreateRequest(
                    name = FuncName("funcName"),
                    inputs = FuncInputs(),
                    code = ValueCode("createCode"),
                    codeType = CodeType.Lua54
                )
            )
        )

        repeat(10) {
            awaitCompleted(
                updateFunc(
                    createResponse.id, ApiFuncUpdateRequest(
                        name = FuncName("test-update"), code = ValueCode("code-${it}")
                    )
                )
            )
        }


        val invocationResponse =
            httpTemplate.post("/v1/funcs/{funcId}/invoke").path("funcId", createResponse.id).body(
                ApiFuncInvokeRequest(
                    correlationId = CorrelationId("some-correlation-id"),
                    inputs = InvocationInputs(),
                    version = null
                )
            ).execute()

        assertThat(invocationResponse.statusCode, equalTo(Accepted))
        require(invocationResponse is HttpSuccessResponse) { "request was not successful" }

        val result = invocationResponse.result(ApiExecInvokeRequested::class)
        awaitCompleted(result)

        with(execQueryRepository.get(result.id)) {
            assertThat(code.version, equalTo(CodeVersion(11)))
        }
    }

    @Test
    fun `Tries to invoke func with invalid version`() {
        val createResponse = awaitCompleted(
            createFunc(
                ApiFuncCreateRequest(
                    name = FuncName("test"),
                    inputs = FuncInputs(),
                    code = ValueCode(""),
                    codeType = CodeType.Lua54
                )
            )
        )

        val invocationResponse =
            httpTemplate.post("/v1/funcs/{funcId}/invoke").path("funcId", createResponse.id).body(
                ApiFuncInvokeRequest(
                    correlationId = CorrelationId("some-correlation-id"),
                    inputs = InvocationInputs(),
                    version = CodeVersion(10)
                )
            ).execute()

        assertThat(invocationResponse.statusCode, equalTo(NotFound))
        require(invocationResponse is HttpErrorResponse) { "request was successful" }

        val error = invocationResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Code not found"))

        verifyNoRequests(ExecInvokeRequested::class)
    }

    @Test
    fun `Tries to invoke func which does not exist`() {
        val invocationResponse = httpTemplate.post("/v1/funcs/1234/invoke").body(
            ApiFuncInvokeRequest(
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