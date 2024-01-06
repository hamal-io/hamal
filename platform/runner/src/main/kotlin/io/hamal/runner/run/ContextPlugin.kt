package io.hamal.runner.run

import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.extend.plugin.RunnerPluginFactory
import io.hamal.lib.kua.table.TableProxyArray
import io.hamal.lib.kua.type.KuaNil
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.kua.type.KuaMap
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
            KuaNil
        }

        val hook = if (invocation is HookInvocation) {
            KuaMap(
                "method" to KuaString(invocation.method.toString()),
                "headers" to invocation.headers.value,
                "parameters" to invocation.parameters.value,
                "content" to invocation.content.value
            )
        } else {
            KuaNil
        }

        val endpoint = if (invocation is EndpointInvocation) {
            KuaMap(
                "method" to KuaString(invocation.method.toString()),
                "headers" to invocation.headers.value,
                "parameters" to invocation.parameters.value,
                "content" to invocation.content.value
            )
        } else {
            KuaNil
        }

        return RunnerPlugin(
            name = "context",
            internals = mapOf(
                "events" to events,
                "hook" to hook,
                "endpoint" to endpoint,
                "exec_id" to KuaString(executionCtx[ExecId::class].value.value.toString(16)),
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
                KuaMap(
                    mutableMapOf(
                        "id" to KuaString(it.id.value.value.toString(16)),
                        "topic" to KuaMap(
                            mutableMapOf(
                                "id" to KuaString(it.topic.id.value.value.toString(16)),
                                "name" to KuaString(it.topic.name.value)
                            )
                        ),
                        "payload" to it.payload.value
                    )
                )
            )
        }.forEach(result::append)
        result
    }
