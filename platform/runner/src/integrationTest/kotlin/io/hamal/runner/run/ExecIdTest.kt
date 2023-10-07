package io.hamal.runner.run

import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.kua.function.Function0In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.runner.connector.UnitOfWork
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class ExecIdTest : AbstractExecuteTest() {
    @Test
    fun `exec_id available in code`() {
        val testExecutor = createTestExecutor()
        testExecutor.run(
            UnitOfWork(
                id = ExecId(1234),
                groupId = GroupId(5432),
                inputs = ExecInputs(),
                state = State(),
                code = CodeValue("assert(ctx.exec_id == '4d2')"),
                correlation = null
            )
        )
    }

    @Test
    fun `exec_id available in function`() {
        val testFn = TestFunction()

        val testExecutor = createTestExecutor("fn" to testFn)
        testExecutor.run(
            UnitOfWork(
                id = ExecId(1234),
                groupId = GroupId(5432),
                inputs = ExecInputs(),
                state = State(),
                code = CodeValue("test.fn()"),
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