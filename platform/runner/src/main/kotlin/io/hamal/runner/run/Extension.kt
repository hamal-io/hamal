package io.hamal.runner.run

import io.hamal.lib.domain.Event
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extension.BundleExtension
import io.hamal.lib.kua.extension.BundleExtensionFactory
import io.hamal.lib.kua.table.TableProxyArray
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.kua.type.toProxyMap
import io.hamal.runner.run.function.CompleteRunFunction
import io.hamal.runner.run.function.EmitFunction
import io.hamal.runner.run.function.FailRunFunction

class RunnerContextFactory(
    private val executionCtx: RunnerContext
) : BundleExtensionFactory {
    override fun create(sandbox: Sandbox): BundleExtension {
        return BundleExtension(
            name = "context",
            internals = mapOf(
                "events" to sandbox.invocationEvents(executionCtx[RunnerInvocationEvents::class].events),
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