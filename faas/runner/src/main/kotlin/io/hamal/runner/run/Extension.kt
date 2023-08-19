package io.hamal.runner.run

import io.hamal.lib.domain.EventInvocation
import io.hamal.lib.domain.Invocation
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extension.ScriptExtension
import io.hamal.lib.kua.extension.ScriptExtensionFactory
import io.hamal.lib.kua.table.TableTypeArray
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.kua.type.convertTableMap
import io.hamal.runner.extension.ctx.function.EmitFunction

class RunnerContextFactory(
    private val executionCtx: RunnerSandboxContext
) : ScriptExtensionFactory {
    override fun create(sandbox: Sandbox): ScriptExtension {
        return ScriptExtension(
            name = "ctx",
            internals = mapOf(
                "events" to sandbox.invocationEvents(executionCtx[Invocation::class]),
                "exec_id" to StringType(executionCtx[ExecId::class].value.value.toString(16)),
                "emit" to EmitFunction(executionCtx)
            )
        )
    }
}

private fun Sandbox.invocationEvents(invocation: Invocation): TableTypeArray = if (invocation is EventInvocation) {
    tableCreateArray(invocation.events.size).let { result ->
        invocation.events.map { convertTableMap(it.value) }.forEach(result::append)
        result
    }
} else {
    tableCreateArray(0)
}