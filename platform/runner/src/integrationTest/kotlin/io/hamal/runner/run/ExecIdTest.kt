package io.hamal.runner.run

import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.function.Function0In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.runner.connector.UnitOfWork
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class ExecIdTest : AbstractExecuteTest() {
    @Test
    fun `exec id available in code`() {
        val testExecutor = createTestRunner()
        testExecutor.run(
            UnitOfWork(
                id = ExecId(1234),
                flowId = FlowId(9876),
                groupId = GroupId(5432),
                inputs = ExecInputs(),
                state = State(),
                code = CodeValue("assert(context.exec.id == '4d2')"),
                correlation = null,
                apiHost = ApiHost("http://test-api")
            )
        )
    }

    @Test
    fun `exec id available in function`() {
        val testFn = TestFunction()

        val testExecutor = createTestRunner("fn" to testFn)
        testExecutor.run(
            UnitOfWork(
                id = ExecId(1234),
                flowId = FlowId(9876),
                groupId = GroupId(5432),
                inputs = ExecInputs(),
                state = State(),
                code = CodeValue("require('test').fn()"),
                correlation = null,
                apiHost = ApiHost("http://test-api")
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