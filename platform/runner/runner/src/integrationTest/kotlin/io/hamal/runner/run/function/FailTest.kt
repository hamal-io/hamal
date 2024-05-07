package io.hamal.runner.run.function

import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.common.value.ValueError
import io.hamal.lib.common.value.ValueObject
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.domain.State
import io.hamal.lib.domain._enum.CodeTypes.Lua54
import io.hamal.lib.domain.vo.CodeType.Companion.CodeType
import io.hamal.lib.domain.vo.ExecId.Companion.ExecId
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.domain.vo.ExecResult
import io.hamal.lib.domain.vo.ExecStatusCode.Companion.ExecStatusCode
import io.hamal.lib.domain.vo.ExecToken.Companion.ExecToken
import io.hamal.lib.domain.vo.NamespaceId.Companion.NamespaceId
import io.hamal.lib.domain.vo.TriggerId.Companion.TriggerId
import io.hamal.lib.domain.vo.WorkspaceId.Companion.WorkspaceId
import io.hamal.lib.kua.function.Function0In1Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput1Schema
import io.hamal.runner.connector.UnitOfWork
import io.hamal.runner.run.AbstractTest
import io.hamal.runner.test.TestFailConnector
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class FailTest : AbstractTest() {

    @Test
    fun `Invoking fail interrupts execution`() {
        runTest(
            unitOfWork(
                """
            context.fail()
            context.complete({ status_code = 200, result = {} })
        """.trimIndent()
            ),
            TestFailConnector()
        )
    }

    @Test
    fun `Fails execution with error`() {
        runTest(
            unitOfWork(
                """
            test = require_plugin('test')
            err = test.returns_error()
            context.fail(err)
        """
            ),
            TestFailConnector { execId, statusCode, result ->
                assertThat(execId, equalTo(ExecId(1234)))
                assertThat(statusCode, equalTo(ExecStatusCode(500)))
                assertThat(result, equalTo(ExecResult(ValueObject.builder().set("value", ValueError("Sometimes an error can be a good thing")).build())))
            },
            mapOf(ValueString("returns_error") to FunctionReturnsError()),
        )
    }

    @Test
    fun `Fails execution without argument`() {
        runTest(
            unitOfWork("context.fail()"),
            TestFailConnector { execId, statusCode, result ->
                assertThat(execId, equalTo(ExecId(1234)))
                assertThat(statusCode, equalTo(ExecStatusCode(500)))
                assertThat(result, equalTo(ExecResult(ValueObject.empty)))
            }
        )
    }


    @Test
    fun `Fails execution with string argument`() {
        runTest(
            unitOfWork("context.fail('test')"),
            TestFailConnector { execId, statusCode, result ->
                assertThat(execId, equalTo(ExecId(1234)))
                assertThat(statusCode, equalTo(ExecStatusCode(500)))
                assertThat(result, equalTo(ExecResult(ValueObject.builder().set("value", "test").build())))
            }
        )
    }

    @Test
    fun `Fails execution with number argument`() {
        runTest(
            unitOfWork("context.fail(1337)"),
            TestFailConnector { execId, statusCode, result ->
                assertThat(execId, equalTo(ExecId(1234)))
                assertThat(statusCode, equalTo(ExecStatusCode(500)))
                assertThat(result, equalTo(ExecResult(ValueObject.builder().set("value", 1337).build())))
            }
        )
    }

    @Test
    fun `Fails execution with boolean argument`() {
        runTest(
            unitOfWork("context.fail(false)"),
            TestFailConnector { execId, statusCode, result ->
                assertThat(execId, equalTo(ExecId(1234)))
                assertThat(statusCode, equalTo(ExecStatusCode(500)))
                assertThat(result, equalTo(ExecResult(ValueObject.builder().set("value", false).build())))
            }
        )
    }

    @Test
    fun `Fails execution table argument`() {
        runTest(
            unitOfWork("context.fail({ status_code = 532, result = {reason = 'undisclosed', answer = 42} })"),
            TestFailConnector { execId, statusCode, result ->
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
        )
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
        triggerId = TriggerId(4567),
        inputs = ExecInputs(),
        state = State(),
        code = ValueCode(code),
        codeType = CodeType(Lua54),
        correlation = null
    )
}