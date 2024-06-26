package io.hamal.runner.run

import io.hamal.lib.common.value.ValueNil
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.extend.plugin.RunnerPluginFactory
import io.hamal.lib.kua.value.toKua
import io.hamal.lib.kua.value.toKuaSnakeCase
import io.hamal.runner.run.function.CompleteRunFunction
import io.hamal.runner.run.function.EmitFunction
import io.hamal.runner.run.function.FailRunFunction

class RunnerContextFactory(
    private val executionCtx: RunnerContext
) : RunnerPluginFactory {

    override fun create(sandbox: Sandbox): RunnerPlugin {
        val inputs = executionCtx[ExecInputs::class].value.toKuaSnakeCase(sandbox)
        return RunnerPlugin(
            name = ValueString("context"),
            internals = mapOf(
                ValueString("inputs") to inputs,
                ValueString("exec_id") to ValueString(executionCtx[ExecId::class].stringValue),
                ValueString("exec_token") to ValueString(executionCtx[ExecToken::class].stringValue),
                ValueString("trigger_id") to (executionCtx.find(TriggerId::class)?.stringValue?.let(::ValueString) ?: ValueNil),
                ValueString("emit") to EmitFunction(executionCtx),
                ValueString("fail") to FailRunFunction,
                ValueString("complete") to CompleteRunFunction,
                ValueString("state") to executionCtx.state.value.toKua(sandbox),
                ValueString("env") to executionCtx[RunnerEnv::class].value.toKua(sandbox),
                ValueString("namespace_id") to ValueString(executionCtx[NamespaceId::class].stringValue),
                ValueString("workspace_id") to ValueString(executionCtx[WorkspaceId::class].stringValue)
            )
        )
    }
}
