package io.hamal.runner.run

import io.hamal.lib.domain.vo.Event
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.Invocation
import io.hamal.lib.domain.vo.RunnerEnv
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.extend.plugin.RunnerPluginFactory
import io.hamal.lib.kua.createTable
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
            sandbox.createTable(
                "method" to KuaString(invocation.method.toString()),
                "headers" to invocation.headers.value.toKua(sandbox),
                "parameters" to invocation.parameters.value.toKua(sandbox),
                "content" to invocation.content.value.toKua(sandbox)
            )
        } else {
            KuaNil
        }

        val endpoint = if (invocation is Invocation.Endpoint) {
            sandbox.createTable(
                "method" to KuaString(invocation.method.toString()),
                "headers" to invocation.headers.value.toKua(sandbox),
                "parameters" to invocation.parameters.value.toKua(sandbox),
                "content" to invocation.content.value.toKua(sandbox)
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
                "state" to executionCtx.state.value.toKua(sandbox),
                "env" to executionCtx[RunnerEnv::class].value.toKua(sandbox)
            )
        )
    }
}

private fun Sandbox.invocationEvents(events: List<Event>): KuaTable =
//    tableCreate(events.size).let { result ->
//        events.map {
//            toTableProxy(
//                KuaTable(
//                    mutableMapOf(
//                        "id" to KuaString(it.id.value.value.toString(16)),
//                        "topic" to KuaTable(
//                            mutableMapOf(
//                                "id" to KuaString(it.topic.id.value.value.toString(16)),
//                                "name" to KuaString(it.topic.name.value)
//                            )
//                        ),
//                        "payload" to it.payload.value.toKua()
//                    )
//                )
//            )
//        }.forEach(result::append)
//        result
    TODO()
//}
