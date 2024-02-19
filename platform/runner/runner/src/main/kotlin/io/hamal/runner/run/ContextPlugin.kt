package io.hamal.runner.run

import io.hamal.lib.domain.vo.Event
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.Invocation
import io.hamal.lib.domain.vo.RunnerEnv
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.extend.plugin.RunnerPluginFactory
import io.hamal.lib.kua.table.TableProxyArray
import io.hamal.lib.kua.type.*
import io.hamal.runner.run.function.CompleteRunFunction
import io.hamal.runner.run.function.EmitFunction
import io.hamal.runner.run.function.FailRunFunction

class RunnerContextFactory(
    private val executionCtx: RunnerContext
) : RunnerPluginFactory {

    override fun create(sandbox: Sandbox): RunnerPlugin {
        val invocation = executionCtx[Invocation::class]

        val events = if (invocation is Invocation.Event) {
            sandbox.invocationEvents(invocation.events)
        } else {
            KuaNil
        }

        val hook = if (invocation is Invocation.Hook) {
            KuaMap(
                "method" to KuaString(invocation.method.toString()),
                "headers" to invocation.headers.value.toKua(),
                "parameters" to invocation.parameters.value.toKua(),
                "content" to invocation.content.value.toKua()
            )
        } else {
            KuaNil
        }

        val endpoint = if (invocation is Invocation.Endpoint) {
            KuaMap(
                "method" to KuaString(invocation.method.toString()),
                "headers" to invocation.headers.value.toKua(),
                "parameters" to invocation.parameters.value.toKua(),
                "content" to invocation.content.value.toKua()
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
                "state" to executionCtx.state.value.toKua(),
                "env" to executionCtx[RunnerEnv::class].value.toKua()
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
                        "payload" to it.payload.value.toKua()
                    )
                )
            )
        }.forEach(result::append)
        result
    }
