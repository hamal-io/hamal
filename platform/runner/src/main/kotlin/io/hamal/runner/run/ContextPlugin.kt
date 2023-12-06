package io.hamal.runner.run

import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.extend.plugin.RunnerPluginFactory
import io.hamal.lib.kua.table.TableProxyArray
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.NilType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.kua.type.toProxyMap
import io.hamal.runner.run.function.CompleteRunFunction
import io.hamal.runner.run.function.EmitFunction
import io.hamal.runner.run.function.FailRunFunction

class RunnerContextFactory(
    private val executionCtx: RunnerContext
) : RunnerPluginFactory {

    override fun create(sandbox: Sandbox): RunnerPlugin {
        val invocation = executionCtx[Invocation::class]

        val events = if (invocation is EventInvocation) {
            sandbox.invocationEvents(invocation.events)
        } else {
            NilType
        }

        val hook = if (invocation is HookInvocation) {
            MapType(
                "method" to StringType(invocation.method.toString()),
                "headers" to invocation.headers.value,
                "parameters" to invocation.parameters.value,
                "content" to invocation.content.value
            )
        } else {
            NilType
        }

        val endpoint = if (invocation is EndpointInvocation) {
            MapType(
                "method" to StringType(invocation.method.toString()),
                "headers" to invocation.headers.value,
                "parameters" to invocation.parameters.value,
                "content" to invocation.content.value
            )
        } else {
            NilType
        }

        return RunnerPlugin(
            name = "context",
            internals = mapOf(
                "api" to MapType(
                    "host" to StringType(executionCtx[ApiHost::class].value),
                ),
                "events" to events,
                "hook" to hook,
                "endpoint" to endpoint,
                "exec_id" to StringType(executionCtx[ExecId::class].value.value.toString(16)),
                "emit" to EmitFunction(executionCtx),
                "fail" to FailRunFunction,
                "complete" to CompleteRunFunction,
                "state" to executionCtx.state.value,
                "env" to executionCtx[RunnerEnv::class].value
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
