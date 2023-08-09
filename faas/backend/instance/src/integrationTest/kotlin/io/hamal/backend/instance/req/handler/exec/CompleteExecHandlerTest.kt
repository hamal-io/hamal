package io.hamal.backend.instance.req.handler.exec

import io.hamal.backend.instance.req.handler.BaseReqHandlerTest
import io.hamal.backend.repository.api.CompletedExec
import io.hamal.lib.domain.Event
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.State
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.req.SubmittedCompleteExecReq
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecStatus
import io.hamal.lib.domain.vo.ExecStatus.Completed
import io.hamal.lib.domain.vo.ExecStatus.Started
import io.hamal.lib.kua.value.NumberValue
import io.hamal.lib.kua.value.StringValue
import io.hamal.lib.kua.value.TableValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

internal class CompleteExecHandlerTest : BaseReqHandlerTest() {

    @Test
    fun `Completes exec`() {
        createExec(ExecId(1234), Started)
        testInstance(submittedCompleteExecReq)
        verifyCompleted()
    }

    @Test
    fun `Tries to complete exec, but does not exists`() {
        val exception = assertThrows<NoSuchElementException> {
            testInstance(submittedCompleteExecReq)
        }
        assertThat(exception.message, equalTo("Exec not found"))
    }

    @TestFactory
    fun `Tries to complete exec, but not in status Started`() = ExecStatus.values()
        .filterNot { it == Started }
        .map { execStatus ->
            dynamicTest("Can not complete: $execStatus") {
                val execId = generateDomainId(::ExecId)
                createExec(execId, execStatus)

                val exception = assertThrows<IllegalArgumentException> {
                    testInstance(submittedCompleteExecReq.copy(id = execId))
                }
                assertThat(exception.message, equalTo("Exec not in status Started"))

                verifyNoCOmpletedExecExists()
            }
        }

    @Autowired
    private lateinit var testInstance: CompleteExecHandler

    private val submittedCompleteExecReq = SubmittedCompleteExecReq(
        reqId = ReqId(10),
        status = ReqStatus.Submitted,
        id = ExecId(1234),
        state = State(TableValue("counter" to NumberValue(1))),
        events = listOf(
            Event(
                TableValue(
                    "topic" to StringValue("test-completion"),
                    "ich" to StringValue("habFertsch")
                )
            )
        ),
    )

    private fun verifyCompleted() {
        execQueryRepository.list { }.also { execs ->
            assertThat(execs, hasSize(1))
            with(execs.first()) {
                require(this is CompletedExec)
                assertThat(id, equalTo(ExecId(1234)))
                assertThat(status, equalTo(Completed))
            }
        }
    }

    private fun verifyNoCOmpletedExecExists() {
        execQueryRepository.list { }.also { execs ->
            assertThat(execs, hasSize(1))
            with(execs.first()) {
                assertThat(status, not(equalTo(Completed)))
            }
        }
    }
}