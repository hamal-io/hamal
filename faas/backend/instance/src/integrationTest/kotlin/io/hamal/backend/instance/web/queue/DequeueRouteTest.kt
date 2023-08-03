package io.hamal.backend.instance.web.queue

import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.value.CodeValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

internal class DequeueRouteTest : BaseQueueRouteTest() {
    @Test
    fun `Nothing to dequeue`() {
        with(dequeue()) {
            assertThat(execs, empty())
        }
    }

    @Test
    fun `Dequeues single exec without correlation`() {
        createExec(
            execId = generateDomainId(::ExecId),
            status = ExecStatus.Queued,
            code = CodeValue("40 + 2")
        )
        with(dequeue()) {
            assertThat(execs, hasSize(1))

            with(execs.first()) {
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
            status = ExecStatus.Queued,
            correlation = Correlation(
                funcId = FuncId(123),
                correlationId = CorrelationId("_some_chosen_correlation_@")
            ),
            code = CodeValue("40 + 2")
        )
        with(dequeue()) {
            assertThat(execs, hasSize(1))

            with(execs.first()) {
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

private fun DequeueRouteTest.verifyExecStarted(execId: ExecId) {
    assertThat(execQueryRepository.get(execId).status, equalTo(ExecStatus.Started))
}