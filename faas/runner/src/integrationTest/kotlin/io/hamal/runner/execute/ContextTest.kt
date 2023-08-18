package io.hamal.runner.execute

import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.kua.function.Function0In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.type.CodeType
import io.hamal.runner.connector.UnitOfWork
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class ExecIdTest : AbstractExecuteTest() {
    @Test
    fun `exec_id available in code`() {
        val testExecutor = createTestExecutor()
        testExecutor(
            UnitOfWork(
                id = ExecId(1234),
                inputs = ExecInputs(),
                state = State(),
                code = CodeType("test.capture_string(ctx.exec_id)"),
                correlation = null
            )
        )
        assertThat(stringCaptor.result, equalTo("4d2"))
    }

    @Test
    fun `exec_id available in function`() {
        val testFn = TestFunction()

        val testExecutor = createTestExecutor("fn" to testFn)
        testExecutor(
            UnitOfWork(
                id = ExecId(1234),
                inputs = ExecInputs(),
                state = State(),
                code = CodeType("test.fn()"),
                correlation = null
            )
        )
        assertThat(testFn.result, equalTo("4d2"))
    }

    class TestFunction(var result: String? = null) : Function0In0Out() {
        override fun invoke(ctx: FunctionContext) {
            result = ctx[ExecId::class].value.value.toString(16)
        }
    }
}