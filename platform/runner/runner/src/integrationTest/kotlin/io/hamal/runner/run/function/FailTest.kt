package io.hamal.runner.run.function

import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.common.value.ValueError
import io.hamal.lib.common.value.ValueObject
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.domain.State
import io.hamal.lib.domain._enum.CodeType
import io.hamal.lib.domain.vo.ExecId.Companion.ExecId
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.domain.vo.ExecResult
import io.hamal.lib.domain.vo.ExecStatusCode.Companion.ExecStatusCode
import io.hamal.lib.domain.vo.ExecToken.Companion.ExecToken
import io.hamal.lib.domain.vo.NamespaceId.Companion.NamespaceId
import io.hamal.lib.domain.vo.WorkspaceId.Companion.WorkspaceId
import io.hamal.lib.kua.function.Function0In1Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput1Schema
import io.hamal.runner.connector.UnitOfWork
import io.hamal.runner.run.AbstractExecuteTest
import io.hamal.runner.test.TestFailConnector
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class FailTest : AbstractExecuteTest() {

    @Test
    fun `Invoking fail interrupts execution`() {
        createTestRunner(
            connector = TestFailConnector()
        ).also { runner ->
            runner.run(
                unitOfWork(
                    """
            context.fail()
            context.complete({ status_code = 200, result = {} })
        """.trimIndent()
                )
            )
        }
    }

    @Test
    fun `Fails execution with error`() {
        createTestRunner(
            testPlugins = arrayOf(ValueString("returns_error") to FunctionReturnsError()),
            connector = TestFailConnector { execId, statusCode, result ->
                assertThat(execId, equalTo(ExecId(1234)))
                assertThat(statusCode, equalTo(ExecStatusCode(500)))
                assertThat(result, equalTo(ExecResult(ValueObject.builder().set("value", ValueError("Sometimes an error can be a good thing")).build())))
            }
        ).also { runner ->
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
    }

    @Test
    fun `Fails execution without argument`() {
        createTestRunner(
            connector = TestFailConnector { execId, statusCode, result ->
                assertThat(execId, equalTo(ExecId(1234)))
                assertThat(statusCode, equalTo(ExecStatusCode(500)))
                assertThat(result, equalTo(ExecResult(ValueObject.empty)))
            }
        ).also { runner ->
            runner.run(unitOfWork("context.fail()"))
        }
    }


    @Test
    fun `Fails execution with string argument`() {
        createTestRunner(
            connector = TestFailConnector { execId, statusCode, result ->
                assertThat(execId, equalTo(ExecId(1234)))
                assertThat(statusCode, equalTo(ExecStatusCode(500)))
                assertThat(result, equalTo(ExecResult(ValueObject.builder().set("value", "test").build())))
            }
        ).also { runner ->
            runner.run(unitOfWork("context.fail('test')"))
        }
    }

    @Test
    fun `Fails execution with number argument`() {
        createTestRunner(
            connector = TestFailConnector { execId, statusCode, result ->
                assertThat(execId, equalTo(ExecId(1234)))
                assertThat(statusCode, equalTo(ExecStatusCode(500)))
                assertThat(result, equalTo(ExecResult(ValueObject.builder().set("value", 1337).build())))
            }
        ).also { runner ->
            runner.run(unitOfWork("context.fail(1337)"))
        }
    }

    @Test
    fun `Fails execution with boolean argument`() {
        createTestRunner(
            connector = TestFailConnector { execId, statusCode, result ->
                assertThat(execId, equalTo(ExecId(1234)))
                assertThat(statusCode, equalTo(ExecStatusCode(500)))
                assertThat(result, equalTo(ExecResult(ValueObject.builder().set("value", false).build())))
            }
        ).also { runner ->
            runner.run(unitOfWork("context.fail(false)"))
        }
    }

    @Test
    fun `Fails execution table argument`() {
        createTestRunner(
            connector = TestFailConnector { execId, statusCode, result ->
                assertThat(execId, equalTo(ExecId(1234)))
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
            }
        ).also { runner ->
            runner.run(unitOfWork("context.fail({ status_code = 532, result = {reason = 'undisclosed', answer = 42} })"))
        }
    }

    private class FunctionReturnsError : Function0In1Out<ValueError>(
        FunctionOutput1Schema(ValueError::class)
    ) {
        override fun invoke(ctx: FunctionContext): ValueError {
            return ValueError("Sometimes an error can be a good thing")
        }
    }

    private fun unitOfWork(code: String) = UnitOfWork(
        id = ExecId(1234),
        execToken = ExecToken("ExecToken"),
        namespaceId = NamespaceId(9876),
        workspaceId = WorkspaceId(5432),
        inputs = ExecInputs(),
        state = State(),
        code = ValueCode(code),
        codeType = CodeType.Lua54,
        correlation = null
    )
}