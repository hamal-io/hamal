package io.hamal.bridge.http.controller.exec

import io.hamal.lib.common.value.ValueObject
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain._enum.ExecStates
import io.hamal.lib.domain._enum.ExecStates.Failed
import io.hamal.lib.domain._enum.ExecStates.Started
import io.hamal.lib.domain.vo.CorrelationId.Companion.CorrelationId
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecId.Companion.ExecId
import io.hamal.lib.domain.vo.ExecResult
import io.hamal.lib.domain.vo.ExecStatusCode.Companion.ExecStatusCode
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.bridge.BridgeExecFailRequest
import io.hamal.lib.sdk.bridge.BridgeExecFailRequested
import io.hamal.repository.api.Exec
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

internal class ExecFailControllerTest : BaseExecControllerTest() {

    @TestFactory
    fun `Can not fail exec which is not started`() = ExecStates.values()
        .filterNot { it == Started }
        .map { execStatus ->
            dynamicTest("Can not fail: $execStatus") {
                val exec = createExec(
                    execId = generateDomainId(::ExecId),
                    status = execStatus,
                    correlation = Correlation(
                        funcId = generateDomainId(::FuncId),
                        id = CorrelationId("__correlation__")
                    )
                )

                val failureResponse = requestFailure(exec.id)
                assertThat(failureResponse.statusCode, equalTo(Accepted))
                require(failureResponse is HttpSuccessResponse) { "request was not successful" }

                val result = failureResponse.result(BridgeExecFailRequested::class)
                awaitFailed(result.id)
            }
        }

    @Test
    fun `Fails started exec`() {
        val startedExec = createExec(
            execId = ExecId(123),
            status = Started,
            correlation = Correlation(
                funcId = generateDomainId(::FuncId),
                id = CorrelationId("__correlation__")
            )
        ) as Exec.Started

        val failureResponse = requestFailure(startedExec.id)
        assertThat(failureResponse.statusCode, equalTo(Accepted))
        require(failureResponse is HttpSuccessResponse) { "request was not successful" }

        val result = failureResponse.result(BridgeExecFailRequested::class)
        awaitCompleted(result.id)

        verifyExecFailed(result.execId)
        //FIXME events
    }


    @Test
    fun `Tries to fail exec which does not exist`() {
        val response = httpTemplate.post("/b1/execs/123456765432/fail")
            .body(
                BridgeExecFailRequest(
                    ExecStatusCode(444),
                    ExecResult(ValueObject.builder().set("message", "SomeErrorValue").build())
                )
            ).execute()

        assertThat(response.statusCode, equalTo(NotFound))
        require(response is HttpErrorResponse) { "request was successful" }

        val result = response.error(ApiError::class)
        assertThat(result.message, equalTo("Exec not found"))
    }

    private fun verifyExecFailed(execId: ExecId) {
        with(execQueryRepository.get(execId) as Exec.Failed) {
            assertThat(id, equalTo(execId))
            assertThat(status, equalTo(Failed))
            assertThat(statusCode, equalTo(ExecStatusCode(443)))
            assertThat(result, equalTo(ExecResult(ValueObject.builder().set("message", "SomeErrorCause").build())))
        }
    }

    private fun requestFailure(execId: ExecId) =
        httpTemplate.post("/b1/execs/{execId}/fail")
            .path("execId", execId)
            .body(
                BridgeExecFailRequest(
                    ExecStatusCode(443),
                    ExecResult(ValueObject.builder().set("message", "SomeErrorCause").build())
                )
            )
            .execute()

}