package io.hamal.runner.run.function

import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.function.Function0In1Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput1Schema
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaString
import io.hamal.runner.connector.UnitOfWork
import io.hamal.runner.run.AbstractExecuteTest
import io.hamal.runner.test.TestFailConnector
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class FailTest : AbstractExecuteTest() {

    @Test
    fun `Invoking fail interrupts execution`() {
        val runner = createTestRunner(
            connector = TestFailConnector()
        )
        runner.run(
            unitOfWork(
                """
            context.fail()
            context.complete()
        """.trimIndent()
            )
        )
    }

    @Test
    fun `Fails execution with error`() {
        val runner = createTestRunner(
            testPlugins = arrayOf(
                KuaString("returns_error") to FunctionReturnsError(),
            ),
            connector = TestFailConnector { execId, execResult ->
                assertThat(execId, equalTo(ExecId(1234)))
                assertThat(
                    execResult,
                    equalTo(
                        ExecResult(
                            HotObject.builder().set("message", "Sometimes an error can be a good thing").build()
                        )
                    )
                )
            }
        )
        runner.run(
            unitOfWork(
                """
            test = require_plugin('test')
            err = test.returns_error()
            context.fail(err)
        """
            )
        )
    }

    @Test
    fun `Fails execution without argument`() {
        val runner = createTestRunner(
            connector = TestFailConnector { execId, execResult ->
                assertThat(execId, equalTo(ExecId(1234)))
                assertThat(execResult, equalTo(ExecResult(HotObject.empty)))
            }
        )
        runner.run(unitOfWork("context.fail()"))
    }


    @Test
    fun `Fails execution with string argument`() {
        val runner = createTestRunner(
            connector = TestFailConnector { execId, execResult ->
                assertThat(execId, equalTo(ExecId(1234)))
                assertThat(execResult, equalTo(ExecResult(HotObject.builder().set("message", "test").build())))
            }
        )
        runner.run(unitOfWork("context.fail('test')"))
    }

    @Test
    fun `Fails execution with number argument`() {
        val runner = createTestRunner(
            connector = TestFailConnector { execId, execResult ->
                assertThat(execId, equalTo(ExecId(1234)))
                assertThat(execResult, equalTo(ExecResult(HotObject.builder().set("value", 1337).build())))
            }
        )
        runner.run(unitOfWork("context.fail(1337)"))
    }

    @Test
    fun `Fails execution with boolean argument`() {
        val runner = createTestRunner(
            connector = TestFailConnector { execId, execResult ->
                assertThat(execId, equalTo(ExecId(1234)))
                assertThat(execResult, equalTo(ExecResult(HotObject.builder().set("value", false).build())))
            }
        )
        runner.run(unitOfWork("context.fail(false)"))
    }

    @Test
    fun `Fails execution table argument`() {
        val runner = createTestRunner(
            connector = TestFailConnector { execId, execResult ->
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
            }
        )
        runner.run(unitOfWork("context.fail({reason = 'undisclosed', answer = 42})"))
    }

    private class FunctionReturnsError : Function0In1Out<KuaError>(
        FunctionOutput1Schema(KuaError::class)
    ) {
        override fun invoke(ctx: FunctionContext): KuaError {
            return KuaError("Sometimes an error can be a good thing")
        }
    }

    private fun unitOfWork(code: String) = UnitOfWork(
        id = ExecId(1234),
        execToken = ExecToken("ExecToken"),
        namespaceId = NamespaceId(9876),
        workspaceId = WorkspaceId(5432),
        inputs = ExecInputs(),
        state = State(),
        code = CodeValue(code),
        correlation = null
    )
}