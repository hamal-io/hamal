package io.hamal.runner.run

import io.hamal.lib.domain.Event
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extension.ScriptExtension
import io.hamal.lib.kua.extension.ScriptExtensionFactory
import io.hamal.lib.kua.table.TableProxyArray
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.kua.type.toProxyMap
import io.hamal.runner.extension.ctx.function.EmitFunction

class RunnerContextFactory(
    private val executionCtx: ExecContext
) : ScriptExtensionFactory {
    override fun create(sandbox: Sandbox): ScriptExtension {
        return ScriptExtension(
            name = "ctx",
            internals = mapOf(
                "events" to sandbox.invocationEvents(executionCtx[ExecInvocationEvents::class].events),
                "exec_id" to StringType(executionCtx[ExecId::class].value.value.toString(16)),
                "emit" to EmitFunction(executionCtx)
            )
        )
    }
}

private fun Sandbox.invocationEvents(events: List<Event>): TableProxyArray =
    tableCreateArray(events.size).let { result ->
        events.map {
            toProxyMap(
                MapType(
                    mutableMapOf(
                        "id" to StringType(it.id.value.value.toString(16)),
                        "topic" to MapType(
                            mutableMapOf(
                                "id" to StringType(it.topic.id.value.value.toString(16)),
                                "name" to StringType(it.topic.name.value)
                            )
                        ),
                        "payload" to it.payload.value
                    )
                )
            )
        }.forEach(result::append)
        result
    }