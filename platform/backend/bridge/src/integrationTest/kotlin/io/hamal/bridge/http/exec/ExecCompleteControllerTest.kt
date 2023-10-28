package io.hamal.bridge.http.exec

import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.EventPayload
import io.hamal.lib.domain.EventToSubmit
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.ExecStatus.Started
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.NumberType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.api.ApiCompleteExecReq
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiSubmittedReqWithId
import io.hamal.repository.api.CompletedExec
import io.hamal.repository.api.log.ConsumerId
import io.hamal.repository.api.log.ProtobufBatchConsumer
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
                        correlationId = CorrelationId("__correlation__")
                    )
                )

                val completionResponse = requestCompletion(exec.id)
                assertThat(completionResponse.statusCode, equalTo(Accepted))
                require(completionResponse is SuccessHttpResponse) { "request was not successful" }

                val result = completionResponse.result(ApiSubmittedReqWithId::class)

                awaitFailed(result.reqId)
                verifyNoStateSet(result.id(::ExecId))
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
                correlationId = CorrelationId("__correlation__")
            )
        ) as io.hamal.repository.api.StartedExec

        val completionResponse = requestCompletion(startedExec.id)
        assertThat(completionResponse.statusCode, equalTo(Accepted))
        require(completionResponse is SuccessHttpResponse) { "request was not successful" }

        val result = completionResponse.result(ApiSubmittedReqWithId::class)
        awaitCompleted(result.reqId)

        verifyExecCompleted(result.id(::ExecId))
        verifyStateSet(result.id(::ExecId))
        verifyEventAppended()
    }


    @Test
    fun `Tries to complete exec which does not exist`() {
        val response = httpTemplate.post("/b1/execs/123456765432/complete")
            .body(
                ApiCompleteExecReq(
                    state = State(),
                    result = ExecResult(),
                    events = listOf()
                )
            )
            .execute()

        assertThat(response.statusCode, equalTo(NotFound))
        require(response is ErrorHttpResponse) { "request was successful" }

        val result = response.error(ApiError::class)
        assertThat(result.message, equalTo("Exec not found"))
    }

    private fun verifyExecCompleted(execId: ExecId) {
        with(execQueryRepository.get(execId) as CompletedExec) {
            assertThat(id, equalTo(execId))
            assertThat(status, equalTo(ExecStatus.Completed))
            assertThat(result, equalTo(ExecResult(MapType("hamal" to StringType("rocks")))))
        }
    }

    private fun verifyStateSet(execId: ExecId) {
        val exec = (execQueryRepository.get(execId) as CompletedExec)
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

    private fun verifyEventAppended() {
        val topic = eventBrokerRepository.resolveTopic(testNamespace.id, TopicName("test-completion"))!!

        ProtobufBatchConsumer(
            consumerId = ConsumerId("a"),
            repository = eventBrokerRepository,
            topic = topic,
            valueClass = EventPayload::class
        ).consumeBatch(10) { eventPayloads ->
            assertThat(eventPayloads, hasSize(1))

            val payload = eventPayloads.first()
            assertThat(payload, equalTo(EventPayload(MapType(mutableMapOf("value" to NumberType(42))))))
        }
    }

    private fun requestCompletion(execId: ExecId) =
        httpTemplate.post("/b1/execs/{execId}/complete")
            .path("execId", execId)
            .body(
                ApiCompleteExecReq(
                    state = State(MapType(mutableMapOf("value" to NumberType(13.37)))),
                    result = ExecResult(MapType("hamal" to StringType("rocks"))),
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