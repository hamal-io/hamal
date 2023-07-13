package io.hamal.backend.instance.web.exec

import io.hamal.lib.domain.CompletedExec
import io.hamal.lib.domain.Event
import io.hamal.lib.domain.StartedExec
import io.hamal.lib.domain.State
import io.hamal.lib.domain.req.CompleteExecReq
import io.hamal.lib.domain.req.SubmittedCompleteExecReq
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecStatus
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.script.api.value.NumberValue
import io.hamal.lib.script.api.value.TableValue
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
                val exec = createExec(generateDomainId(::ExecId), execStatus)

                val completionResponse = requestCompletion(exec.id)
                assertThat(completionResponse.statusCode, equalTo(HttpStatusCode.Accepted))
                require(completionResponse is SuccessHttpResponse) { "request was not successful" }

                val result = completionResponse.result(SubmittedCompleteExecReq::class)

                awaitFailed(result.id)
                // FIXME verify state not updated
                // FIXME verify event not emitted
            }
        }

    @Test
    fun `Complete started exec`() {
        val startedExec = createExec(ExecId(123), ExecStatus.Started) as StartedExec

        val completionResponse = requestCompletion(startedExec.id)
        assertThat(completionResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(completionResponse is SuccessHttpResponse) { "request was not successful" }

        val result = completionResponse.result(SubmittedCompleteExecReq::class)
        awaitCompleted(result.id)

        verifyExecCompleted(result.execId)
        //FIXME verify state updated
        //FIXME verify events emitted
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
        with(execQueryRepository.find(execId)!! as CompletedExec) {
            assertThat(id, equalTo(execId))
            assertThat(status, equalTo(ExecStatus.Completed))
        }
    }

    private fun requestCompletion(execId: ExecId) =
        httpTemplate.post("/v1/execs/${execId.value}/complete")
            .body(
                CompleteExecReq(
                    state = State(TableValue("value" to NumberValue("13.37"))),
                    events = listOf(
                        Event(TableValue("value" to NumberValue(42)))
                    )
                )
            )
            .execute()

}