package io.hamal.core.request.handler.exec

import io.hamal.core.request.handler.BaseReqHandlerTest
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.ExecCompleteRequested
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.ExecStatus.Completed
import io.hamal.lib.domain.vo.ExecStatus.Started
import io.hamal.lib.kua.type.KuaMap
import io.hamal.lib.kua.type.KuaNumber
import io.hamal.lib.kua.type.KuaString
import io.hamal.repository.api.ExecQueryRepository.ExecQuery
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

internal class ExecCompleteHandlerTest : BaseReqHandlerTest() {

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
                    testInstance(submittedCompleteExecReq.copy(execId = execId))
                }
                assertThat(exception.message, equalTo("Exec not in status Started"))

                verifyNoCompletedExecExists()
            }
        }

    @Autowired
    private lateinit var testInstance: ExecCompleteHandler

    private val submittedCompleteExecReq by lazy {
        ExecCompleteRequested(
            id = RequestId(10),
            status = RequestStatus.Submitted,
            execId = ExecId(1234),
            result = ExecResult(KuaMap("hamal" to KuaString("rocks"))),
            state = ExecState(KuaMap("counter" to KuaNumber(1))),
            events = listOf(
                EventToSubmit(
                    topicName = TopicName("test-completion"),
                    payload = EventPayload(KuaMap(mutableMapOf("ich" to KuaString("habFertsch"))))
                )
            ),
        )
    }

    private fun verifyCompleted() {
        execQueryRepository.list(ExecQuery(groupIds = listOf())).also { execs ->
            assertThat(execs, hasSize(1))
            with(execs.first()) {
                require(this is io.hamal.repository.api.CompletedExec)
                assertThat(id, equalTo(ExecId(1234)))
                assertThat(status, equalTo(Completed))
                assertThat(result, equalTo(ExecResult(KuaMap("hamal" to KuaString("rocks")))))
            }
        }
    }

    private fun verifyNoCompletedExecExists() {
        execQueryRepository.list(ExecQuery(groupIds = listOf())).also { execs ->
            assertThat(execs, hasSize(1))
            with(execs.first()) {
                assertThat(status, not(equalTo(Completed)))
            }
        }
    }
}