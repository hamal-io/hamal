package io.hamal.backend.instance.web.queue

import io.hamal.lib.domain.req.InvokeAdhocReq
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.domain.vo.ExecStatus
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.script.api.value.CodeValue
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
    fun `Dequeues single exec`() {
        awaitCompleted(
            adhoc(
                InvokeAdhocReq(
                    inputs = InvocationInputs(),
                    code = CodeValue("1 + 1")
                )
            )
        )

        with(dequeue()) {
            assertThat(execs, hasSize(1))

            with(execs.first()) {
                assertThat(inputs, equalTo(ExecInputs()))
                assertThat(correlation, nullValue())
                assertThat(code, equalTo(CodeValue("1 + 1")))

                verifyExecStarted(id)
            }
        }
    }
}

private fun DequeueRouteTest.verifyExecStarted(execId: ExecId) {
    assertThat(execQueryRepository.get(execId).status, equalTo(ExecStatus.Started))
}