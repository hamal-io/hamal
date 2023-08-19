package io.hamal.runner.run

import io.hamal.lib.domain.Event
import io.hamal.lib.domain.EventInvocation
import io.hamal.lib.domain.Invocation
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.kua.extension.ScriptExtension
import io.hamal.lib.kua.extension.ScriptExtensionFactory
import io.hamal.lib.kua.type.StringType
import io.hamal.runner.extension.ctx.function.EmitFunction

class RunnerContextFactory(
    private val executionContext: RunnerSandboxContext
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

private fun Invocation.triggerEvents(): List<Event> {
    return if (this is EventInvocation) {
        events
    } else {
        listOf()
    }
}