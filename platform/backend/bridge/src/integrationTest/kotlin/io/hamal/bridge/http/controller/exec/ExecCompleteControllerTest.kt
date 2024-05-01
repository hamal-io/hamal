package io.hamal.bridge.http.controller.exec

import io.hamal.lib.common.domain.BatchSize
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.EventToSubmit
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.CorrelationId.Companion.CorrelationId
import io.hamal.lib.domain.vo.ExecStatus.Started
import io.hamal.lib.domain.vo.TopicName.Companion.TopicName
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.bridge.BridgeExecCompleteRequest
import io.hamal.lib.sdk.bridge.BridgeExecCompleteRequested
import io.hamal.repository.api.Exec
import io.hamal.repository.api.log.LogConsumerBatchImpl
import io.hamal.repository.api.log.LogConsumerId
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

internal class ExecCompleteControllerTest : BaseExecControllerTest() {

    @TestFactory
    fun `Can not complete exec which is not started`() = ExecStatus.values()
        .filterNot { it == Started }
        .map { execStatus ->
            dynamicTest("Can not complete: $execStatus") {
                val exec = createExec(
                    execId = generateDomainId(::ExecId),
                    status = execStatus,
                    correlation = Correlation(
                        funcId = generateDomainId(::FuncId),
                        id = CorrelationId("__correlation__")
                    )
                )

                val completionResponse = requestCompletion(exec.id)
                assertThat(completionResponse.statusCode, equalTo(Accepted))
                require(completionResponse is HttpSuccessResponse) { "request was not successful" }

                val result = completionResponse.result(BridgeExecCompleteRequested::class)

                awaitFailed(result.id)
                verifyNoStateSet(result.execId)
                // FIXME verify event not emitted
            }
        }

    @Test
    fun `Completes started exec`() {
        val startedExec = createExec(
            execId = ExecId(123),
            status = Started,
            correlation = Correlation(
                funcId = generateDomainId(::FuncId),
                id = CorrelationId("__correlation__")
            )
        ) as Exec.Started

        val completionResponse = requestCompletion(startedExec.id)
        assertThat(completionResponse.statusCode, equalTo(Accepted))
        require(completionResponse is HttpSuccessResponse) { "request was not successful" }

        val result = completionResponse.result(BridgeExecCompleteRequested::class)
        awaitCompleted(result.id)

        verifyExecCompleted(result.execId)
        verifyStateSet(result.execId)
        verifyEventAppended()
    }


    @Test
    fun `Tries to complete exec which does not exist`() {
        val response = httpTemplate.post("/b1/execs/123456765432/complete")
            .body(
                BridgeExecCompleteRequest(
                    state = ExecState(),
                    result = ExecResult(),
                    events = listOf()
                )
            )
            .execute()

        assertThat(response.statusCode, equalTo(NotFound))
        require(response is HttpErrorResponse) { "request was successful" }

        val result = response.error(ApiError::class)
        assertThat(result.message, equalTo("Exec not found"))
    }

    private fun verifyExecCompleted(execId: ExecId) {
        with(execQueryRepository.get(execId) as Exec.Completed) {
            assertThat(id, equalTo(execId))
            assertThat(status, equalTo(ExecStatus.Completed))
            assertThat(result, equalTo(ExecResult(HotObject.builder().set("hamal", "rocks").build())))
        }
    }

    private fun verifyStateSet(execId: ExecId) {
        val exec = (execQueryRepository.get(execId) as Exec.Completed)
        with(stateQueryRepository.get(exec.correlation!!)) {
            assertThat(correlation, equalTo(exec.correlation))
            assertThat(value, equalTo(State(HotObject.builder().set("value", 13.37).build())))
        }
    }

    private fun verifyNoStateSet(execId: ExecId) {
        val exec = (execQueryRepository.get(execId))
        with(stateQueryRepository.get(exec.correlation!!)) {
            assertThat(correlation, equalTo(exec.correlation))
            assertThat(value, equalTo(State()))
        }
    }

    private fun verifyEventAppended() {
        val topic = topicQueryRepository.getTopic(testNamespace.id, TopicName("test-completion"))

        LogConsumerBatchImpl(
            consumerId = LogConsumerId(123),
            repository = logBrokerRepository,
            topicId = topic.logTopicId,
            valueClass = EventPayload::class
        ).consumeBatch(BatchSize(10)) { eventPayloads ->
            assertThat(eventPayloads, hasSize(1))

            val payload = eventPayloads.first()
            assertThat(payload, equalTo(EventPayload(HotObject.builder().set("value", 42).build())))
        }
    }

    private fun requestCompletion(execId: ExecId) =
        httpTemplate.post("/b1/execs/{execId}/complete")
            .path("execId", execId)
            .body(
                BridgeExecCompleteRequest(
                    state = ExecState(HotObject.builder().set("value", 13.37).build()),
                    result = ExecResult(HotObject.builder().set("hamal", "rocks").build()),
                    events = listOf(
                        EventToSubmit(
                            topicName = TopicName("test-completion"),
                            payload = EventPayload(HotObject.builder().set("value", 42).build())
                        )
                    )
                )
            )
            .execute()

}