package io.hamal.backend.instance.req.handler.exec

import io.hamal.backend.instance.req.handler.BaseReqHandlerTest
import io.hamal.repository.api.FailedExec
import io.hamal.backend.repository.api.submitted_req.SubmittedFailExecReq
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecStatus
import io.hamal.lib.domain.vo.ExecStatus.Failed
import io.hamal.lib.domain.vo.ExecStatus.Started
import io.hamal.lib.kua.type.ErrorType
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

internal class FailExecHandlerTest : BaseReqHandlerTest() {

    @Test
    fun `Fail exec`() {
        createExec(ExecId(1234), Started)
        testInstance(submittedFailExecReq)
        verifyFailed()
    }

    @Test
    fun `Tries to fail exec, but does not exists`() {
        val exception = assertThrows<NoSuchElementException> {
            testInstance(submittedFailExecReq)
        }
        assertThat(exception.message, equalTo("Exec not found"))
    }

    @TestFactory
    fun `Tries to fail exec, but not in status Started`() = ExecStatus.values()
        .filterNot { it == Started }
        .map { execStatus ->
            dynamicTest("Can not complete: $execStatus") {
                val execId = generateDomainId(::ExecId)
                createExec(execId, execStatus)

                val exception = assertThrows<IllegalArgumentException> {
                    testInstance(submittedFailExecReq.copy(id = execId))
                }
                assertThat(exception.message, equalTo("Exec not in status Started"))

                verifyNoFailedExecExists()
            }
        }

    @Autowired
    private lateinit var testInstance: FailExecHandler

    private val submittedFailExecReq = SubmittedFailExecReq(
        reqId = ReqId(10),
        status = ReqStatus.Submitted,
        id = ExecId(1234),
        cause = ErrorType("You have not tried hard enough")
    )

    private fun verifyFailed() {
        execQueryRepository.list { }.also { execs ->
            assertThat(execs, hasSize(1))
            with(execs.first()) {
                require(this is io.hamal.repository.api.FailedExec)
                assertThat(id, equalTo(ExecId(1234)))
                assertThat(status, equalTo(Failed))
            }
        }
    }

    private fun verifyNoFailedExecExists() {
        execQueryRepository.list { }.also { execs ->
            assertThat(execs, hasSize(1))
            with(execs.first()) {
                assertThat(status, not(equalTo(Failed)))
            }
        }
    }
}