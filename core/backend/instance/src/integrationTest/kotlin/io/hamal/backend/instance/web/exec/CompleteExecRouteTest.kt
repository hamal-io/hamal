package io.hamal.backend.instance.web.exec

import io.hamal.lib.domain.*
import io.hamal.lib.domain.req.CompleteExecReq
import io.hamal.lib.domain.req.SubmittedCompleteExecReq
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecStatus
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.kua.value.NumberValue
import io.hamal.lib.kua.value.TableValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

internal class CompleteExecRouteTest : BaseExecRouteTest() {

    @TestFactory
    fun `Can not complete exec which is not started`() = ExecStatus.values()
        .filterNot { it == ExecStatus.Started }
        .map { execStatus ->
            dynamicTest("Can not complete: $execStatus") {
                val exec = createExec(
                    execId = generateDomainId(::ExecId),
                    status = execStatus,
                    correlation = Correlation(
                        funcId = generateDomainId(::FuncId),
                        correlationId = CorrelationId("__correlation__")
                    )
                )

                val completionResponse = requestCompletion(exec.id)
                assertThat(completionResponse.statusCode, equalTo(HttpStatusCode.Accepted))
                require(completionResponse is SuccessHttpResponse) { "request was not successful" }

                val result = completionResponse.result(SubmittedCompleteExecReq::class)

                awaitFailed(result.id)
                verifyNoStateSet(result.execId)
                // FIXME verify event not emitted
            }
        }

    @Test
    fun `Completes started exec`() {
        val startedExec = createExec(
            execId = ExecId(123),
            status = ExecStatus.Started,
            correlation = Correlation(
                funcId = generateDomainId(::FuncId),
                correlationId = CorrelationId("__correlation__")
            )
        ) as StartedExec

        val completionResponse = requestCompletion(startedExec.id)
        assertThat(completionResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(completionResponse is SuccessHttpResponse) { "request was not successful" }

        val result = completionResponse.result(SubmittedCompleteExecReq::class)
        awaitCompleted(result.id)

        verifyExecCompleted(result.execId)
        verifyStateSet(result.execId)
        //FIXME events
    }


    @Test
    fun `Tries to complete exec which does not exist`() {
        val response = httpTemplate.post("/v1/execs/123456765432/complete")
            .body(
                CompleteExecReq(
                    state = State(),
                    events = listOf()
                )
            )
            .execute()

        assertThat(response.statusCode, equalTo(HttpStatusCode.Accepted))
        require(response is SuccessHttpResponse) { "request was not successful" }

        val result = response.result(SubmittedCompleteExecReq::class)
        awaitFailed(result.id)
    }

    private fun verifyExecCompleted(execId: ExecId) {
        with(execQueryRepository.get(execId) as CompletedExec) {
            assertThat(id, equalTo(execId))
            assertThat(status, equalTo(ExecStatus.Completed))
        }
    }

    private fun verifyStateSet(execId: ExecId) {
        val exec = (execQueryRepository.get(execId) as CompletedExec)
        with(stateQueryRepository.get(exec.correlation!!)) {
            assertThat(correlation, equalTo(exec.correlation))
            assertThat(value, equalTo(State(TableValue("value" to NumberValue(13.37)))))
        }
    }

    private fun verifyNoStateSet(execId: ExecId) {
        val exec = (execQueryRepository.get(execId))
        with(stateQueryRepository.get(exec.correlation!!)) {
            assertThat(correlation, equalTo(exec.correlation))
            assertThat(value, equalTo(State()))
        }
    }

    private fun requestCompletion(execId: ExecId) =
        httpTemplate.post("/v1/execs/${execId.value}/complete")
            .body(
                CompleteExecReq(
                    state = State(TableValue("value" to NumberValue(13.37))),
                    events = listOf(Event(TableValue("value" to NumberValue(42))))
                )
            )
            .execute()

}