package io.hamal.runner.run

import io.hamal.lib.domain.vo.Event
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.Invocation
import io.hamal.lib.domain.vo.RunnerEnv
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.extend.plugin.RunnerPluginFactory
import io.hamal.lib.kua.tableCreate
import io.hamal.lib.kua.type.KuaNil
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.kua.type.KuaTable
import io.hamal.lib.kua.type.toKua
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
            sandbox.tableCreate(
                KuaString("method") to KuaString(invocation.method.toString()),
                KuaString("headers") to invocation.headers.value.toKua(sandbox),
                KuaString("parameters") to invocation.parameters.value.toKua(sandbox),
                KuaString("content") to invocation.content.value.toKua(sandbox)
            )
        } else {
            KuaNil
        }

        val endpoint = if (invocation is Invocation.Endpoint) {
            sandbox.tableCreate(
                KuaString("method") to KuaString(invocation.method.toString()),
                KuaString("headers") to invocation.headers.value.toKua(sandbox),
                KuaString("parameters") to invocation.parameters.value.toKua(sandbox),
                KuaString("content") to invocation.content.value.toKua(sandbox)
            )
        } else {
            KuaNil
        }

        return RunnerPlugin(
            name = KuaString("context"),
            internals = mapOf(
                KuaString("events") to events,
                KuaString("hook") to hook,
                KuaString("endpoint") to endpoint,
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

private fun Sandbox.invocationEvents(events: List<Event>): KuaTable =
    tableCreate(
        events.map { evt ->
            tableCreate(
                KuaString("id") to KuaString(evt.id.value.value.toString(16)),
                KuaString("topic") to tableCreate(
                    KuaString("id") to KuaString(evt.topic.id.value.value.toString(16)),
                    KuaString("name") to KuaString(evt.topic.name.value)
                ),
                KuaString("payload") to evt.payload.value.toKua(this)
            )
        }
    )

