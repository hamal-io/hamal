package io.hamal.bridge.http.exec

import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.bridge.BridgeExecFailReq
import io.hamal.lib.sdk.bridge.BridgeExecFailSubmitted
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

internal class ExecFailControllerTest : BaseExecControllerTest() {

    @TestFactory
    fun `Can not fail exec which is not started`() = ExecStatus.values()
        .filterNot { it == ExecStatus.Started }
        .map { execStatus ->
            dynamicTest("Can not fail: $execStatus") {
                val exec = createExec(
                    execId = generateDomainId(::ExecId),
                    status = execStatus,
                    correlation = Correlation(
                        funcId = generateDomainId(::FuncId),
                        correlationId = CorrelationId("__correlation__")
                    )
                )

                val failureResponse = requestFailure(exec.id)
                assertThat(failureResponse.statusCode, equalTo(Accepted))
                require(failureResponse is HttpSuccessResponse) { "request was not successful" }

                val result = failureResponse.result(BridgeExecFailSubmitted::class)
                awaitFailed(result.id)
            }
        }

    @Test
    fun `Fails started exec`() {
        val startedExec = createExec(
            execId = ExecId(123),
            status = ExecStatus.Started,
            correlation = Correlation(
                funcId = generateDomainId(::FuncId),
                correlationId = CorrelationId("__correlation__")
            )
        ) as io.hamal.repository.api.StartedExec

        val failureResponse = requestFailure(startedExec.id)
        assertThat(failureResponse.statusCode, equalTo(Accepted))
        require(failureResponse is HttpSuccessResponse) { "request was not successful" }

        val result = failureResponse.result(BridgeExecFailSubmitted::class)
        awaitCompleted(result.id)

        verifyExecFailed(result.execId)
        //FIXME events
    }


    @Test
    fun `Tries to fail exec which does not exist`() {
        val response = httpTemplate.post("/b1/execs/123456765432/fail")
            .body(BridgeExecFailReq(ExecResult(MapType("message" to StringType("SomeErrorValue")))))
            .execute()

        assertThat(response.statusCode, equalTo(NotFound))
        require(response is HttpErrorResponse) { "request was successful" }

        val result = response.error(ApiError::class)
        assertThat(result.message, equalTo("Exec not found"))
    }

    private fun verifyExecFailed(execId: ExecId) {
        with(execQueryRepository.get(execId) as io.hamal.repository.api.FailedExec) {
            assertThat(id, equalTo(execId))
            assertThat(status, equalTo(ExecStatus.Failed))
            assertThat(result, equalTo(ExecResult(MapType("message" to StringType("SomeErrorCause")))))
        }
    }

    private fun requestFailure(execId: ExecId) =
        httpTemplate.post("/b1/execs/{execId}/fail")
            .path("execId", execId)
            .body(BridgeExecFailReq(ExecResult(MapType("message" to StringType("SomeErrorCause")))))
            .execute()

}