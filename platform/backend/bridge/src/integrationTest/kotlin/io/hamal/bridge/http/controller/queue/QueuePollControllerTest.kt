package io.hamal.bridge.http.controller.queue

import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain._enum.ExecStates.Queued
import io.hamal.lib.domain._enum.ExecStates.Started
import io.hamal.lib.domain.vo.CodeValue.Companion.CodeValue
import io.hamal.lib.domain.vo.CorrelationId.Companion.CorrelationId
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.domain.vo.ExecToken.Companion.ExecToken
import io.hamal.lib.domain.vo.FuncId.Companion.FuncId
import io.hamal.lib.domain.vo.TriggerId.Companion.TriggerId
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
            code = CodeValue("40 + 2")
        )
        with(dequeue()) {
            assertThat(work, hasSize(1))

            with(work.first()) {
                assertThat(execToken, equalTo(ExecToken("ExecToken")))
                assertThat(namespaceId, equalTo(testNamespace.id))
                assertThat(workspaceId, equalTo(testWorkspace.id))
                assertThat(triggerId, equalTo(TriggerId(23)))
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
                id = CorrelationId("_some_chosen_correlation_@")
            ),
            code = CodeValue("40 + 2")
        )
        with(dequeue()) {
            assertThat(work, hasSize(1))

            with(work.first()) {
                assertThat(execToken, equalTo(ExecToken("ExecToken")))
                assertThat(namespaceId, equalTo(testNamespace.id))
                assertThat(workspaceId, equalTo(testWorkspace.id))
                assertThat(triggerId, equalTo(TriggerId(23)))
                assertThat(inputs, equalTo(ExecInputs()))
                assertThat(correlation, equalTo(Correlation(funcId = FuncId(123),id = CorrelationId("_some_chosen_correlation_@"))))
                assertThat(code, equalTo(CodeValue("40 + 2")))

                verifyExecStarted(id)
            }
        }
    }
}

private fun QueuePollControllerTest.verifyExecStarted(execId: ExecId) {
    assertThat(execQueryRepository.get(execId).status, equalTo(Started))
}