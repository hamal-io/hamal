package io.hamal.runner.run.function

import io.hamal.lib.common.value.ValueObject
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.domain.vo.ExecResult
import io.hamal.lib.domain.vo.ExecStatusCode.Companion.ExecStatusCode
import io.hamal.lib.kua.function.Function0In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.runner.run.AbstractTest
import io.hamal.runner.test.RunnerFixture.unitOfWork
import io.hamal.runner.test.TestFailConnector
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

internal class OnTest : AbstractTest() {

    @Test
    fun `Without parameter`() {
        runTest(
            unitOfWork("""context.on()""".trimIndent()),
            TestFailConnector { _, statusCode, result ->
                assertThat(statusCode, equalTo(ExecStatusCode(400)))
                assertThat(result, equalTo(ExecResult(ValueObject.builder().set("message", "event is not set").build())))
            }
        )
    }

    @Test
    fun `Event not supported`() {
        runTest(
            unitOfWork("""context.on('will_never_be_supported', function() end)""".trimIndent()),
            TestFailConnector { _, statusCode, result ->
                assertThat(statusCode, equalTo(ExecStatusCode(400)))
                assertThat(result, equalTo(ExecResult(ValueObject.builder().set("message", "will_never_be_supported not supported yet").build())))
            }
        )
    }

    @Test
    fun `Without function`() {
        runTest(
            unitOfWork("""context.on('completed')""".trimIndent()),
            TestFailConnector { _, statusCode, result ->
                assertThat(statusCode, equalTo(ExecStatusCode(400)))
                assertThat(result, equalTo(ExecResult(ValueObject.builder().set("message", "function is not set").build())))
            }
        )
    }

    @Test
    fun `Not a function`() {
        runTest(
            unitOfWork("""context.on('completed', 123)""".trimIndent()),
            TestFailConnector { _, statusCode, result ->
                assertThat(statusCode, equalTo(ExecStatusCode(400)))
                assertThat(result, equalTo(ExecResult(ValueObject.builder().set("message", "Expected function but got number").build())))
            }
        )
    }

    @Test
    fun `Invokes on completed function when explicitly calling complete`() {
        val invokeFunction = TestInvokeFunction()
        runTest(
            unitOfWork("""context.on('completed', function() require_plugin('test').invoke() end); context.complete()""".trimIndent()),
            testPlugins = mapOf(ValueString("invoke") to invokeFunction)
        )

        assertThat(invokeFunction.result, equalTo(true))
    }

    @Test
    fun `Invokes on completed function without explicitly calling complete`() {
        val invokeFunction = TestInvokeFunction()
        runTest(
            unitOfWork("""context.on('completed', function() require_plugin('test').invoke() end)""".trimIndent()),
            testPlugins = mapOf(ValueString("invoke") to invokeFunction)
        )

        assertThat(invokeFunction.result, equalTo(true))
    }

    class TestInvokeFunction(var result: Boolean = false) : Function0In0Out() {
        override fun invoke(ctx: FunctionContext) {
            result = true
        }
    }
}