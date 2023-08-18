package io.hamal.runner.execute

import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.kua.extension.ScriptExtension
import io.hamal.lib.kua.extension.ScriptExtensionFactory
import io.hamal.lib.kua.type.StringType
import io.hamal.runner.extension.ctx.function.EmitFunction

class ExecutionContextFactory(
    private val executionContext: SandboxExecutionContext
) : ScriptExtensionFactory {
    override fun create(): ScriptExtension {
        return ScriptExtension(
            name = "ctx",
            internals = mapOf(
                "exec_id" to StringType(executionContext[ExecId::class].value.value.toString(16)),
                "emit" to EmitFunction(executionContext)
            )
        )
    }
}