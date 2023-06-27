package io.hamal.backend.instance.web.exec

import io.hamal.lib.domain.CompletedExec
import io.hamal.lib.domain.Event
import io.hamal.lib.domain.StartedExec
import io.hamal.lib.domain.State
import io.hamal.lib.domain.req.CompleteExecReq
import io.hamal.lib.domain.req.SubmittedCompleteExecReq
import io.hamal.lib.domain.vo.Content
import io.hamal.lib.domain.vo.ContentType
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecStatus
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

internal class CompleteExecRouteIT : BaseExecRouteIT() {
    @TestFactory
    fun `Can not complete exec which is not started`() = ExecStatus.values()
        .filterNot { it == ExecStatus.Started }
        .map { execStatus ->
            dynamicTest("Can not complete: $execStatus") {
                val exec = createExec(execStatus)

                val completionResponse = requestCompletion(exec.id)
                assertThat(completionResponse.statusCode, equalTo(HttpStatusCode.Accepted))
                require(completionResponse is SuccessHttpResponse) { "request was not successful" }

                val result = completionResponse.result(SubmittedCompleteExecReq::class)

                awaitReqFailed(result.id)
                // FIXME verify state not updated
                // FIXME verify event not emitted
            }
        }

    @Test
    fun `Complete started exec`() {
        val startedExec = createExec(ExecStatus.Started) as StartedExec

        val completionResponse = requestCompletion(startedExec.id)
        assertThat(completionResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(completionResponse is SuccessHttpResponse) { "request was not successful" }

        val result = completionResponse.result(SubmittedCompleteExecReq::class)
        awaitReqCompleted(result.id)

        verifyExecCompleted(result.execId)
        //FIXME verify state updated
        //FIXME verify events emitted
    }

    @Test
    fun `Tries to complete exec which does not exist`() {
        val response = httpTemplate.post("/v1/execs/123456765432/complete")
            .body(
                CompleteExecReq(
                    state = State(
                        contentType = ContentType("text/plain"),
                        content = Content("")
                    ),
                    events = listOf()
                )
            )
            .execute()

        assertThat(response.statusCode, equalTo(HttpStatusCode.Accepted))
        require(response is SuccessHttpResponse) { "request was not successful" }

        val result = response.result(SubmittedCompleteExecReq::class)
        awaitReqFailed(result.id)
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
                    state = State(
                        contentType = ContentType("text/plain"),
                        content = Content("13.37")
                    ),
                    events = listOf(
                        Event(
                            contentType = ContentType("text/plain"),
                            content = Content("42")
                        )
                    )
                )
            )
            .execute()

}