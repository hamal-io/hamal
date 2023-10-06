package io.hamal.bridge.web.work

import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.domain.vo.ExecStatus.Queued
import io.hamal.lib.domain.vo.ExecStatus.Started
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.kua.type.CodeType
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

internal class DequeueControllerTest : BaseWorkControllerTest() {
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
            code = CodeType("40 + 2")
        )
        with(dequeue()) {
            assertThat(work, hasSize(1))

            with(work.first()) {
                assertThat(inputs, equalTo(ExecInputs()))
                assertThat(correlation, nullValue())
                assertThat(code, equalTo(CodeType("40 + 2")))

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
            code = CodeType("40 + 2")
        )
        with(dequeue()) {
            assertThat(work, hasSize(1))

            with(work.first()) {
                assertThat(inputs, equalTo(ExecInputs()))
                assertThat(
                    correlation, equalTo(
                        Correlation(
                            funcId = FuncId(123),
                            correlationId = CorrelationId("_some_chosen_correlation_@")
                        )
                    )
                )
                assertThat(code, equalTo(CodeType("40 + 2")))

                verifyExecStarted(id)
            }
        }
    }
}

private fun DequeueControllerTest.verifyExecStarted(execId: ExecId) {
    assertThat(execQueryRepository.get(execId).status, equalTo(Started))
}