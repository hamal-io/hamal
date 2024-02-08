package io.hamal.bridge.http.controller.queue

import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.ExecStatus.Queued
import io.hamal.lib.domain.vo.ExecStatus.Started
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

internal class QueuePollControllerTest : BaseQueueControllerTest() {
    @Test
    fun `Nothing to dequeue`() {
        with(dequeue()) {
            assertThat(work, empty())
        }
    }

    @Test
    fun `Dequeues single exec without correlation`() {
        createExec(
            execId = generateDomainId(::ExecId),
            status = Queued,
            code = CodeValue("40 + 2"),
            invocation = EmptyInvocation
        )
        with(dequeue()) {
            assertThat(work, hasSize(1))

            with(work.first()) {
                assertThat(namespaceId, equalTo(testNamespace.id))
                assertThat(groupId, equalTo(testGroup.id))
                assertThat(inputs, equalTo(ExecInputs()))
                assertThat(correlation, nullValue())
                assertThat(code, equalTo(CodeValue("40 + 2")))

                verifyExecStarted(id)
            }
        }
    }

    @Test
    fun `Dequeues single exec with correlation`() {
        createExec(
            execId = generateDomainId(::ExecId),
            status = Queued,
            correlation = Correlation(
                funcId = FuncId(123),
                correlationId = CorrelationId("_some_chosen_correlation_@")
            ),
            code = CodeValue("40 + 2"),
            invocation = EmptyInvocation
        )
        with(dequeue()) {
            assertThat(work, hasSize(1))

            with(work.first()) {
                assertThat(namespaceId, equalTo(testNamespace.id))
                assertThat(groupId, equalTo(testGroup.id))
                assertThat(inputs, equalTo(ExecInputs()))
                assertThat(
                    correlation, equalTo(
                        Correlation(
                            funcId = FuncId(123),
                            correlationId = CorrelationId("_some_chosen_correlation_@")
                        )
                    )
                )
                assertThat(code, equalTo(CodeValue("40 + 2")))

                verifyExecStarted(id)
            }
        }
    }
}

private fun QueuePollControllerTest.verifyExecStarted(execId: ExecId) {
    assertThat(execQueryRepository.get(execId).status, equalTo(Started))
}