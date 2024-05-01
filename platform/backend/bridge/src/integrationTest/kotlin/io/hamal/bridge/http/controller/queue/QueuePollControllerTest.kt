package io.hamal.bridge.http.controller.queue

import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.vo.CorrelationId.Companion.CorrelationId
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.domain.vo.ExecStatus.Queued
import io.hamal.lib.domain.vo.ExecStatus.Started
import io.hamal.lib.domain.vo.ExecToken.Companion.ExecToken
import io.hamal.lib.domain.vo.FuncId.Companion.FuncId
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
            code = ValueCode("40 + 2")
        )
        with(dequeue()) {
            assertThat(work, hasSize(1))

            with(work.first()) {
                assertThat(execToken, equalTo(ExecToken("ExecToken")))
                assertThat(namespaceId, equalTo(testNamespace.id))
                assertThat(workspaceId, equalTo(testWorkspace.id))
                assertThat(inputs, equalTo(ExecInputs()))
                assertThat(correlation, nullValue())
                assertThat(code, equalTo(ValueCode("40 + 2")))

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
            code = ValueCode("40 + 2")
        )
        with(dequeue()) {
            assertThat(work, hasSize(1))

            with(work.first()) {
                assertThat(execToken, equalTo(ExecToken("ExecToken")))
                assertThat(namespaceId, equalTo(testNamespace.id))
                assertThat(workspaceId, equalTo(testWorkspace.id))
                assertThat(inputs, equalTo(ExecInputs()))
                assertThat(
                    correlation, equalTo(
                        Correlation(
                            funcId = FuncId(123),
                            id = CorrelationId("_some_chosen_correlation_@")
                        )
                    )
                )
                assertThat(code, equalTo(ValueCode("40 + 2")))

                verifyExecStarted(id)
            }
        }
    }
}

private fun QueuePollControllerTest.verifyExecStarted(execId: ExecId) {
    assertThat(execQueryRepository.get(execId).status, equalTo(Started))
}