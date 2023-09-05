package io.hamal.api.web.exec

import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.EventPayload
import io.hamal.lib.domain.EventToSubmit
import io.hamal.lib.domain.State
import io.hamal.lib.domain.req.CompleteExecReq
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.NumberType
import io.hamal.lib.sdk.hub.HubSubmittedReqWithId
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
                assertThat(completionResponse.statusCode, equalTo(Accepted))
                require(completionResponse is SuccessHttpResponse) { "request was not successful" }

                val result = completionResponse.result(HubSubmittedReqWithId::class)

                awaitFailed(result.reqId)
                verifyNoStateSet(result.id(::ExecId))
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
        ) as io.hamal.repository.api.StartedExec

        val completionResponse = requestCompletion(startedExec.id)
        assertThat(completionResponse.statusCode, equalTo(Accepted))
        require(completionResponse is SuccessHttpResponse) { "request was not successful" }

        val result = completionResponse.result(HubSubmittedReqWithId::class)
        awaitCompleted(result.reqId)

        verifyExecCompleted(result.id(::ExecId))
        verifyStateSet(result.id(::ExecId))
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

        assertThat(response.statusCode, equalTo(Accepted))
        require(response is SuccessHttpResponse) { "request was not successful" }

        val result = response.result(HubSubmittedReqWithId::class)
        awaitFailed(result.reqId)
    }

    private fun verifyExecCompleted(execId: ExecId) {
        with(execQueryRepository.get(execId) as io.hamal.repository.api.CompletedExec) {
            assertThat(id, equalTo(execId))
            assertThat(status, equalTo(ExecStatus.Completed))
        }
    }

    private fun verifyStateSet(execId: ExecId) {
        val exec = (execQueryRepository.get(execId) as io.hamal.repository.api.CompletedExec)
        with(stateQueryRepository.get(exec.correlation!!)) {
            assertThat(correlation, equalTo(exec.correlation))
            assertThat(value, equalTo(State(MapType(mutableMapOf("value" to NumberType(13.37))))))
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
        httpTemplate.post("/v1/execs/{execId}/complete")
            .path("execId", execId)
            .body(
                CompleteExecReq(
                    state = State(MapType(mutableMapOf("value" to NumberType(13.37)))),
                    events = listOf(
                        EventToSubmit(
                            topicName = TopicName("test-completion"),
                            payload = EventPayload(MapType(mutableMapOf("value" to NumberType(42))))
                        )
                    )
                )
            )
            .execute()

}