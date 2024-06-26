package io.hamal.core.request.handler.exec

import io.hamal.core.request.handler.BaseRequestHandlerTest
import io.hamal.lib.common.value.ValueObject
import io.hamal.lib.domain.EventToSubmit
import io.hamal.lib.domain._enum.ExecStates
import io.hamal.lib.domain._enum.ExecStates.Completed
import io.hamal.lib.domain._enum.ExecStates.Started
import io.hamal.lib.domain._enum.RequestStatuses.Submitted
import io.hamal.lib.domain.request.ExecCompleteRequested
import io.hamal.lib.domain.vo.AuthId.Companion.AuthId
import io.hamal.lib.domain.vo.EventPayload
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecId.Companion.ExecId
import io.hamal.lib.domain.vo.ExecResult
import io.hamal.lib.domain.vo.ExecState
import io.hamal.lib.domain.vo.ExecStatusCode.Companion.ExecStatusCode
import io.hamal.lib.domain.vo.RequestId.Companion.RequestId
import io.hamal.lib.domain.vo.RequestStatus.Companion.RequestStatus
import io.hamal.lib.domain.vo.TopicName.Companion.TopicName
import io.hamal.repository.api.Exec
import io.hamal.repository.api.ExecQueryRepository.ExecQuery
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

internal class ExecCompleteHandlerTest : BaseRequestHandlerTest() {

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
    fun `Tries to complete exec, but not in status Started`() = ExecStates.values()
        .filterNot { it == Started }
        .map { execStatus ->
            dynamicTest("Can not complete: $execStatus") {
                val execId = generateDomainId(::ExecId)
                createExec(execId, execStatus)

                val exception = assertThrows<IllegalArgumentException> {
                    testInstance(submittedCompleteExecReq.copy(id = execId))
                }
                assertThat(exception.message, equalTo("Exec not in status Started"))

                verifyNoCompletedExecExists()
            }
        }

    @Autowired
    private lateinit var testInstance: ExecCompleteHandler

    private val submittedCompleteExecReq by lazy {
        ExecCompleteRequested(
            requestId = RequestId(10),
            requestedBy = AuthId(20),
            requestStatus = RequestStatus(Submitted),
            id = ExecId(1234),
            statusCode = ExecStatusCode(200),
            result = ExecResult(ValueObject.builder().set("hamal", "rocks").build()),
            state = ExecState(ValueObject.builder().set("counter", 1).build()),
            events = listOf(
                EventToSubmit(
                    topicName = TopicName("test-completion"),
                    payload = EventPayload(ValueObject.builder().set("ich", "habFertsch").build())
                )
            ),
        )
    }

    private fun verifyCompleted() {
        execQueryRepository.list(ExecQuery(workspaceIds = listOf())).also { execs ->
            assertThat(execs, hasSize(1))
            with(execs.first()) {
                require(this is Exec.Completed)
                assertThat(id, equalTo(ExecId(1234)))
                assertThat(status, equalTo(Completed))
                assertThat(statusCode, equalTo(ExecStatusCode(200)))
                assertThat(result, equalTo(ExecResult(ValueObject.builder().set("hamal", "rocks").build())))
            }
        }
    }

    private fun verifyNoCompletedExecExists() {
        execQueryRepository.list(ExecQuery(workspaceIds = listOf())).also { execs ->
            assertThat(execs, hasSize(1))
            with(execs.first()) {
                assertThat(status, not(equalTo(Completed)))
            }
        }
    }
}