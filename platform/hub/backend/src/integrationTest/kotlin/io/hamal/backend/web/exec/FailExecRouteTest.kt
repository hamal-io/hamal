package io.hamal.backend.web.exec

import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.req.FailExecReq
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecStatus
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.sdk.hub.HubSubmittedReqWithId
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

internal class FailExecRouteTest : BaseExecRouteTest() {

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

                val result = failureResponse.result(HubSubmittedReqWithId::class)

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

        val result = failureResponse.result(HubSubmittedReqWithId::class)
        awaitCompleted(result.reqId)

        verifyExecFailed(result.id(::ExecId))
        //FIXME events
    }


    @Test
    fun `Tries to fail exec which does not exist`() {
        val response = httpTemplate.post("/v1/execs/123456765432/fail")
            .body(FailExecReq(ErrorType("SomeErrorValue")))
            .execute()

        assertThat(response.statusCode, equalTo(Accepted))
        require(response is SuccessHttpResponse) { "request was not successful" }

        val result = response.result(HubSubmittedReqWithId::class)
        awaitFailed(result.reqId)
    }

    private fun verifyExecFailed(execId: ExecId) {
        with(execQueryRepository.get(execId) as io.hamal.repository.api.FailedExec) {
            assertThat(id, equalTo(execId))
            assertThat(status, equalTo(ExecStatus.Failed))
            assertThat(cause, equalTo(ErrorType("SomeErrorCause")))
        }
    }

    private fun requestFailure(execId: ExecId) =
        httpTemplate.post("/v1/execs/{execId}/fail")
            .path("execId", execId)
            .body(FailExecReq(ErrorType("SomeErrorCause")))
            .execute()

}