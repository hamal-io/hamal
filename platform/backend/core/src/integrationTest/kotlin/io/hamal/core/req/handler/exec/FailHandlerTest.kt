package io.hamal.core.request.handler.exec

import io.hamal.core.request.handler.BaseReqHandlerTest
import io.hamal.lib.domain._enum.RequestStatus.Submitted
import io.hamal.lib.domain.request.ExecFailRequested
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecResult
import io.hamal.lib.domain.vo.ExecStatus
import io.hamal.lib.domain.vo.ExecStatus.Failed
import io.hamal.lib.domain.vo.ExecStatus.Started
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.kua.type.KuaMap
import io.hamal.lib.kua.type.KuaString
import io.hamal.repository.api.ExecQueryRepository.ExecQuery
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired


internal class ExecFailHandlerTest : BaseReqHandlerTest() {

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
                    testInstance(submittedFailExecReq.copy(execId = execId))
                }
                assertThat(exception.message, equalTo("Exec not in status Started"))

                verifyNoFailedExecExists()
            }
        }

    @Autowired
    private lateinit var testInstance: ExecFailHandler

    private val submittedFailExecReq by lazy {
        ExecFailRequested(
            id = RequestId(10),
            status = Submitted,
            execId = ExecId(1234),
            result = ExecResult(KuaMap("message" to KuaString("You have not tried hard enough")))
        )
    }

    private fun verifyFailed() {
        execQueryRepository.list(ExecQuery(groupIds = listOf())).also { execs ->
            assertThat(execs, hasSize(1))
            with(execs.first()) {
                require(this is io.hamal.repository.api.FailedExec)
                assertThat(id, equalTo(ExecId(1234)))
                assertThat(status, equalTo(Failed))
                assertThat(
                    result,
                    equalTo(ExecResult(KuaMap("message" to KuaString("You have not tried hard enough"))))
                )
            }
        }
    }

    private fun verifyNoFailedExecExists() {
        execQueryRepository.list(ExecQuery(groupIds = listOf())).also { execs ->
            assertThat(execs, hasSize(1))
            with(execs.first()) {
                assertThat(status, not(equalTo(Failed)))
            }
        }
    }
}