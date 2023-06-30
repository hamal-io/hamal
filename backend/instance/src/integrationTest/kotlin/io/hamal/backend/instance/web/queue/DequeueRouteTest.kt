package io.hamal.backend.instance.web.queue

import io.hamal.lib.domain.req.InvokeAdhocReq
import io.hamal.lib.domain.vo.*
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
                    code = Code("1 + 1")
                )
            )
        )

        with(dequeue()) {
            assertThat(execs, hasSize(1))

            with(execs.first()) {
                assertThat(inputs, equalTo(ExecInputs()))
                assertThat(correlation, nullValue())
                assertThat(code, equalTo(Code("1 + 1")))

                verifyExecStarted(id)
            }
        }
    }
}

private fun DequeueRouteTest.verifyExecStarted(execId: ExecId) {
    assertThat(execQueryRepository.get(execId).status, equalTo(ExecStatus.Started))
}