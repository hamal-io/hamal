package io.hamal.bridge.web.exec

import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiFailExecReq
import io.hamal.lib.sdk.api.ApiSubmittedReqWithId
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

internal class FailExecControllerTest : BaseExecControllerTest() {

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
                require(failureResponse is SuccessHttpResponse) { "request was not successful" }

                val result = failureResponse.result(ApiSubmittedReqWithId::class)

                awaitFailed(result.reqId)
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
        require(failureResponse is SuccessHttpResponse) { "request was not successful" }

        val result = failureResponse.result(ApiSubmittedReqWithId::class)
        awaitCompleted(result.reqId)

        verifyExecFailed(result.id(::ExecId))
        //FIXME events
    }


    @Test
    fun `Tries to fail exec which does not exist`() {
        val response = httpTemplate.post("/v1/execs/123456765432/fail")
            .body(ApiFailExecReq(ExecResult(MapType("message" to StringType("SomeErrorValue")))))
            .execute()

        assertThat(response.statusCode, equalTo(NotFound))
        require(response is ErrorHttpResponse) { "request was successful" }

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
        httpTemplate.post("/v1/execs/{execId}/fail")
            .path("execId", execId)
            .body(ApiFailExecReq(ExecResult(MapType("message" to StringType("SomeErrorCause")))))
            .execute()

}