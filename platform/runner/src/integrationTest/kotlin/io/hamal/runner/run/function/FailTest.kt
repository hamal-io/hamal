package io.hamal.runner.run.function

import TestFailConnector
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.type.False
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.NumberType
import io.hamal.lib.kua.type.StringType
import io.hamal.runner.connector.UnitOfWork
import io.hamal.runner.run.AbstractExecuteTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class FailTest : AbstractExecuteTest() {

    @Test
    fun `Invoking fail interrupts execution`() {
        val runner = createTestRunner(
            connector = TestFailConnector()
        )
        runner.run(unitOfWork("""
            ctx.fail()
            ctx.complete()
        """.trimIndent()))
    }


    @Test
    fun `Fails execution without argument`() {
        val runner = createTestRunner(
            connector = TestFailConnector { execId, execResult ->
                assertThat(execId, equalTo(ExecId(1234)))
                assertThat(execResult, equalTo(ExecResult(MapType())))
            }
        )
        runner.run(unitOfWork("ctx.fail()"))
    }


    @Test
    fun `Fails execution with string argument`() {
        val runner = createTestRunner(
            connector = TestFailConnector { execId, execResult ->
                assertThat(execId, equalTo(ExecId(1234)))
                assertThat(execResult, equalTo(ExecResult(MapType("message" to StringType("test")))))
            }
        )
        runner.run(unitOfWork("ctx.fail('test')"))
    }

    @Test
    fun `Fails execution with number argument`() {
        val runner = createTestRunner(
            connector = TestFailConnector { execId, execResult ->
                assertThat(execId, equalTo(ExecId(1234)))
                assertThat(execResult, equalTo(ExecResult(MapType("value" to NumberType(1337)))))
            }
        )
        runner.run(unitOfWork("ctx.fail(1337)"))
    }

    @Test
    fun `Fails execution with boolean argument`() {
        val runner = createTestRunner(
            connector = TestFailConnector { execId, execResult ->
                assertThat(execId, equalTo(ExecId(1234)))
                assertThat(execResult, equalTo(ExecResult(MapType("value" to False))))
            }
        )
        runner.run(unitOfWork("ctx.fail(false)"))
    }

    @Test
    fun `Fails execution table argument`() {
        val runner = createTestRunner(
            connector = TestFailConnector { execId, execResult ->
                assertThat(execId, equalTo(ExecId(1234)))
                assertThat(
                    execResult, equalTo(
                        ExecResult(
                            MapType(
                                "reason" to StringType("undisclosed"),
                                "answer" to NumberType(42)
                            )
                        )
                    )
                )
            }
        )
        runner.run(unitOfWork("ctx.fail({reason = 'undisclosed', answer = 42})"))
    }

    private fun unitOfWork(code: String) = UnitOfWork(
        id = ExecId(1234),
        groupId = GroupId(5432),
        inputs = ExecInputs(),
        state = State(),
        code = CodeValue(code),
        correlation = null
    )
}