package io.hamal.backend.instance.web.queue

import io.hamal.lib.domain.req.InvokeAdhocReq
import io.hamal.lib.domain.vo.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

internal class DequeueRouteIT : BaseQueueRouteIT() {
    @Test
    fun `Nothing to dequeue`() {
        with(dequeue()) {
            assertThat(execs, empty())
        }
    }

    @Test
    fun `Dequeues single exec`() {
        adhoc(
            InvokeAdhocReq(
                inputs = InvocationInputs(),
                secrets = InvocationSecrets(),
                code = Code("1 + 1")
            )
        )
        Thread.sleep(10)

        with(dequeue()) {
            assertThat(execs, hasSize(1))

            with(execs.first()) {
                assertThat(inputs, equalTo(ExecInputs()))
                assertThat(secrets, equalTo(ExecSecrets()))
                assertThat(correlation, nullValue())
                assertThat(code, equalTo(Code("1 + 1")))

                verifyExecStarted(id)
            }
        }
    }
}

private fun DequeueRouteIT.verifyExecStarted(execId: ExecId) {
    assertThat(execQueryService.get(execId).status, equalTo(ExecStatus.Started))
}