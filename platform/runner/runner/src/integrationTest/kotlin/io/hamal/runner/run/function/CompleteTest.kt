package io.hamal.runner.run.function

import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.common.value.ValueObject
import io.hamal.lib.domain.State
import io.hamal.lib.domain._enum.CodeTypes
import io.hamal.lib.domain.vo.CodeType
import io.hamal.lib.domain.vo.ExecId.Companion.ExecId
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.domain.vo.ExecResult
import io.hamal.lib.domain.vo.ExecState
import io.hamal.lib.domain.vo.ExecStatusCode.Companion.ExecStatusCode
import io.hamal.lib.domain.vo.ExecToken.Companion.ExecToken
import io.hamal.lib.domain.vo.NamespaceId.Companion.NamespaceId
import io.hamal.lib.domain.vo.WorkspaceId.Companion.WorkspaceId
import io.hamal.runner.connector.UnitOfWork
import io.hamal.runner.run.AbstractTest
import io.hamal.runner.test.TestConnector
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test

internal class CompleteTest : AbstractTest() {

    @Test
    fun `Invoking complete interrupts execution`() {
        runTest(
            unitOfWork(
                """
            context.complete({ status_code = 200, result = { } })
            __throw_internal__('failed')
        """.trimIndent()
            )
        )
    }

    @Test
    fun `Completes execution without argument`() {
        runTest(
            unitOfWork("context.complete()"),
            TestConnector { execId, statusCode, result, state, events ->
                assertThat(execId, equalTo(ExecId(1234)))
                assertThat(statusCode, equalTo(ExecStatusCode(200)))
                assertThat(result, equalTo(ExecResult(ValueObject.empty)))
                assertThat(state, equalTo(ExecState()))
                assertThat(events, hasSize(0))
            }
        )
    }


    @Test
    fun `Completes execution with string argument`() {
        runTest(
            unitOfWork("context.complete('test')"),
            TestConnector { execId, statusCode, result, state, events ->
                assertThat(execId, equalTo(ExecId(1234)))
                assertThat(statusCode, equalTo(ExecStatusCode(200)))
                assertThat(result, equalTo(ExecResult(ValueObject.builder().set("value", "test").build())))
                assertThat(state, equalTo(ExecState()))
                assertThat(events, hasSize(0))
            }
        )
    }

    @Test
    fun `Completes execution with number argument`() {
        runTest(
            unitOfWork("context.complete(1337)"),
            TestConnector { execId, statusCode, result, state, events ->
                assertThat(execId, equalTo(ExecId(1234)))
                assertThat(statusCode, equalTo(ExecStatusCode(200)))
                assertThat(result, equalTo(ExecResult(ValueObject.builder().set("value", 1337).build())))
                assertThat(state, equalTo(ExecState()))
                assertThat(events, hasSize(0))
            }
        )
    }

    @Test
    fun `Completes execution with boolean argument`() {
        runTest(
            unitOfWork("context.complete(false)"),
            TestConnector { execId, statusCode, result, state, events ->
                assertThat(execId, equalTo(ExecId(1234)))
                assertThat(statusCode, equalTo(ExecStatusCode(200)))
                assertThat(result, equalTo(ExecResult(ValueObject.builder().set("value", false).build())))
                assertThat(state, equalTo(ExecState()))
                assertThat(events, hasSize(0))
            }
        )
    }

    @Test
    fun `Completes with table result`() {
        runTest(
            unitOfWork("context.complete({ answer = 42 })"),
            TestConnector { execId, statusCode, result, state, events ->
                assertThat(execId, equalTo(ExecId(1234)))
                assertThat(statusCode, equalTo(ExecStatusCode(200)))
                assertThat(result, equalTo(ExecResult(ValueObject.builder().set("answer", 42).build())))
                assertThat(state, equalTo(ExecState()))
                assertThat(events, hasSize(0))
            }
        )
    }


    @Test
    fun `Completes execution with status code and without result`() {
        runTest(
            unitOfWork("context.complete({ status_code = 204 })"),
            TestConnector { execId, statusCode, result, state, events ->
                assertThat(execId, equalTo(ExecId(1234)))
                assertThat(statusCode, equalTo(ExecStatusCode(204)))
                assertThat(result, equalTo(ExecResult(ValueObject.empty)))
                assertThat(state, equalTo(ExecState()))
                assertThat(events, hasSize(0))
            }
        )
    }

    @Test
    fun `Completes execution with status code and result`() {
        runTest(
            unitOfWork("context.complete({ status_code = 201, result = { reason = 'undisclosed', answer = 42} })"),
            TestConnector { execId, statusCode, result, state, events ->
                assertThat(execId, equalTo(ExecId(1234)))
                assertThat(statusCode, equalTo(ExecStatusCode(201)))
                assertThat(
                    result, equalTo(
                        ExecResult(
                            ValueObject.builder()
                                .set("reason", "undisclosed")
                                .set("answer", 42)
                                .build()
                        )
                    )
                )
                assertThat(state, equalTo(ExecState()))
                assertThat(events, hasSize(0))
            }
        )
    }

    private fun unitOfWork(code: String) = UnitOfWork(
        id = ExecId(1234),
        execToken = ExecToken("ExecToken"),
        namespaceId = NamespaceId(9876),
        workspaceId = WorkspaceId(5432),
        inputs = ExecInputs(),
        state = State(),
        code = ValueCode(code),
        codeType = CodeType.CodeType(CodeTypes.Lua54),
        correlation = null
    )
}