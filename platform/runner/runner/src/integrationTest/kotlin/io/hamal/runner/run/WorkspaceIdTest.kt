package io.hamal.runner.run

import io.hamal.lib.common.value.ValueString
import io.hamal.lib.domain.State
import io.hamal.lib.domain._enum.CodeTypes.Lua54
import io.hamal.lib.domain.vo.CodeType.Companion.CodeType
import io.hamal.lib.domain.vo.CodeValue.Companion.CodeValue
import io.hamal.lib.domain.vo.ExecId.Companion.ExecId
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.domain.vo.ExecToken.Companion.ExecToken
import io.hamal.lib.domain.vo.NamespaceId.Companion.NamespaceId
import io.hamal.lib.domain.vo.TriggerId.Companion.TriggerId
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.domain.vo.WorkspaceId.Companion.WorkspaceId
import io.hamal.lib.kua.function.Function0In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.runner.connector.UnitOfWork
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class WorkspaceIdTest : AbstractTest() {
    @Test
    fun `WorkspaceId available in code`() {
        runTest(
            UnitOfWork(
                id = ExecId(1234),
                execToken = ExecToken("ExecToken"),
                namespaceId = NamespaceId(9876),
                workspaceId = WorkspaceId(5432),
                triggerId = TriggerId(4567),
                inputs = ExecInputs(),
                state = State(),
                code = CodeValue("assert(context.exec.workspace_id == '1538')"),
                codeType = CodeType(Lua54),
                correlation = null
            )
        )
    }

    @Test
    fun `Workspace id available in function`() {
        val testFn = TestFunction()

        runTest(
            UnitOfWork(
                id = ExecId(1234),
                execToken = ExecToken("ExecToken"),
                namespaceId = NamespaceId(9876),
                workspaceId = WorkspaceId(5432),
                triggerId = TriggerId(4567),
                inputs = ExecInputs(),
                state = State(),
                code = CodeValue("require_plugin('test').fn()"),
                codeType = CodeType(Lua54),
                correlation = null
            ),
            testPlugins = mapOf(ValueString("fn") to testFn)
        )

        assertThat(testFn.result, equalTo("1538"))
    }

    class TestFunction(var result: String? = null) : Function0In0Out() {
        override fun invoke(ctx: FunctionContext) {
            result = ctx[WorkspaceId::class].stringValue
        }
    }
}