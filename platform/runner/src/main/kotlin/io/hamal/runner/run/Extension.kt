package io.hamal.runner.run

import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extension.plugin.RunnerPluginExtension
import io.hamal.lib.kua.extension.plugin.RunnerPluginExtensionFactory
import io.hamal.lib.kua.table.TableProxyArray
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.kua.type.toProxyMap
import io.hamal.runner.run.function.CompleteRunFunction
import io.hamal.runner.run.function.EmitFunction
import io.hamal.runner.run.function.FailRunFunction

class RunnerContextFactory(
    private val executionCtx: RunnerContext
) : RunnerPluginExtensionFactory {
    override fun create(sandbox: Sandbox): RunnerPluginExtension {
        val invocation = executionCtx[Invocation::class]
        val events = if (invocation is EventInvocation) {
            invocation.events
        } else {
            listOf()
        }

        return RunnerPluginExtension(
            name = "context",
            internals = mapOf(
                "api" to MapType(
                    "host" to StringType(executionCtx[ApiHost::class].value),
                ),
                "events" to sandbox.invocationEvents(events),
                "exec_id" to StringType(executionCtx[ExecId::class].value.value.toString(16)),
                "emit" to EmitFunction(executionCtx),
                "fail" to FailRunFunction,
                "complete" to CompleteRunFunction,
                "state" to executionCtx.state.value
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