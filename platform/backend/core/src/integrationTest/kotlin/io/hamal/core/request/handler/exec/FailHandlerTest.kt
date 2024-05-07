package io.hamal.core.request.handler.exec

import io.hamal.core.request.handler.BaseRequestHandlerTest
import io.hamal.lib.common.value.ValueObject
import io.hamal.lib.domain._enum.ExecStates
import io.hamal.lib.domain._enum.ExecStates.Failed
import io.hamal.lib.domain._enum.ExecStates.Started
import io.hamal.lib.domain._enum.RequestStatuses.Submitted
import io.hamal.lib.domain.request.ExecFailRequested
import io.hamal.lib.domain.vo.AuthId.Companion.AuthId
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecId.Companion.ExecId
import io.hamal.lib.domain.vo.ExecResult
import io.hamal.lib.domain.vo.ExecStatusCode.Companion.ExecStatusCode
import io.hamal.lib.domain.vo.RequestId.Companion.RequestId
import io.hamal.lib.domain.vo.RequestStatus
import io.hamal.lib.domain.vo.RequestStatus.Companion.RequestStatus
import io.hamal.repository.api.Exec
import io.hamal.repository.api.ExecQueryRepository.ExecQuery
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired


internal class ExecFailHandlerTest : BaseRequestHandlerTest() {

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
    fun `Tries to fail exec, but not in status Started`() = ExecStates.values()
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
    private lateinit var testInstance: ExecFailHandler

    private val submittedFailExecReq by lazy {
        ExecFailRequested(
            requestId = RequestId(10),
            requestedBy = AuthId(20),
            requestStatus = RequestStatus(Submitted),
            id = ExecId(1234),
            statusCode = ExecStatusCode(450),
            result = ExecResult(ValueObject.builder().set("message", "You have not tried hard enough").build())
        )
    }

    private fun verifyFailed() {
        execQueryRepository.list(ExecQuery(workspaceIds = listOf())).also { execs ->
            assertThat(execs, hasSize(1))
            with(execs.first()) {
                require(this is Exec.Failed)
                assertThat(id, equalTo(ExecId(1234)))
                assertThat(status, equalTo(Failed))
                assertThat(statusCode, equalTo(ExecStatusCode(450)))
                assertThat(result, equalTo(ExecResult(ValueObject.builder().set("message", "You have not tried hard enough").build())))
            }
        }
    }

    private fun verifyNoFailedExecExists() {
        execQueryRepository.list(ExecQuery(workspaceIds = listOf())).also { execs ->
            assertThat(execs, hasSize(1))
            with(execs.first()) {
                assertThat(status, not(equalTo(Failed)))
            }
        }
    }
}