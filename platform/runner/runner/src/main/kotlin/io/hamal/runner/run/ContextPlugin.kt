package io.hamal.runner.run

import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.domain.vo.RunnerEnv
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.extend.plugin.RunnerPluginFactory
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.kua.type.toKua
import io.hamal.lib.kua.type.toKuaSnakeCase
import io.hamal.runner.run.function.CompleteRunFunction
import io.hamal.runner.run.function.EmitFunction
import io.hamal.runner.run.function.FailRunFunction

class RunnerContextFactory(
    private val executionCtx: RunnerContext
) : RunnerPluginFactory {

    override fun create(sandbox: Sandbox): RunnerPlugin {
        val inputs = executionCtx[ExecInputs::class].value.toKuaSnakeCase(sandbox)
        return RunnerPlugin(
            name = KuaString("context"),
            internals = mapOf(
                KuaString("inputs") to inputs,
                KuaString("exec_id") to KuaString(executionCtx[ExecId::class].value.value.toString(16)),
                KuaString("emit") to EmitFunction(executionCtx),
                KuaString("fail") to FailRunFunction,
                KuaString("complete") to CompleteRunFunction,
                KuaString("state") to executionCtx.state.value.toKua(sandbox),
                KuaString("env") to executionCtx[RunnerEnv::class].value.toKua(sandbox)
            )
        )
    }
}
