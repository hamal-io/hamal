package io.hamal.runner.run.function

import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.*
import io.hamal.runner.connector.UnitOfWork
import io.hamal.runner.run.AbstractExecuteTest
import io.hamal.runner.test.TestConnector
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test

internal class CompleteTest : AbstractExecuteTest() {

    @Test
    fun `Invoking complete interrupts execution`() {
        val runner = createTestRunner(
            connector = TestConnector()
        )
        runner.run(
            unitOfWork(
                """
            context.complete()
            error('failed')
        """.trimIndent()
            )
        )
    }

    @Test
    fun `Completes execution without argument`() {
        val runner = createTestRunner(
            connector = TestConnector { execId, execResult, state, events ->
                assertThat(execId, equalTo(ExecId(1234)))
                assertThat(execResult, equalTo(ExecResult(HotObject.empty)))
                assertThat(state, equalTo(ExecState()))
                assertThat(events, hasSize(0))
            }
        )
        runner.run(unitOfWork("context.complete()"))
    }


    @Test
    fun `Completes execution with string argument`() {
        val runner = createTestRunner(
            connector = TestConnector { execId, execResult, state, events ->
                assertThat(execId, equalTo(ExecId(1234)))
                assertThat(execResult, equalTo(ExecResult(HotObject.builder().set("value", "test").build())))
                assertThat(state, equalTo(ExecState()))
                assertThat(events, hasSize(0))
            }
        )
        runner.run(unitOfWork("context.complete('test')"))
    }

    @Test
    fun `Completes execution with number argument`() {
        val runner = createTestRunner(
            connector = TestConnector { execId, execResult, state, events ->
                assertThat(execId, equalTo(ExecId(1234)))
                assertThat(execResult, equalTo(ExecResult(HotObject.builder().set("value", 1337).build())))
                assertThat(state, equalTo(ExecState()))
                assertThat(events, hasSize(0))
            }
        )
        runner.run(unitOfWork("context.complete(1337)"))
    }

    @Test
    fun `Completes execution with boolean argument`() {
        val runner = createTestRunner(
            connector = TestConnector { execId, execResult, state, events ->
                assertThat(execId, equalTo(ExecId(1234)))
                assertThat(execResult, equalTo(ExecResult(HotObject.builder().set("value", false).build())))
                assertThat(state, equalTo(ExecState()))
                assertThat(events, hasSize(0))
            }
        )
        runner.run(unitOfWork("context.complete(false)"))
    }

    @Test
    fun `Completes execution table argument`() {
        val runner = createTestRunner(
            connector = TestConnector { execId, execResult, state, events ->
                assertThat(execId, equalTo(ExecId(1234)))
                assertThat(
                    execResult, equalTo(
                        ExecResult(
                            HotObject.builder()
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
        runner.run(unitOfWork("context.complete({reason = 'undisclosed', answer = 42})"))
    }

    private fun unitOfWork(code: String) = UnitOfWork(
        id = ExecId(1234),
        namespaceId = NamespaceId(9876),
        workspaceId = WorkspaceId(5432),
        inputs = ExecInputs(),
        state = State(),
        code = CodeValue(code),
        correlation = null,
        invocation = Invocation.Adhoc
    )
}