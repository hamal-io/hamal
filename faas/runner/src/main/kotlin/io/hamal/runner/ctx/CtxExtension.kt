package io.hamal.runner.ctx

import io.hamal.lib.domain.Event
import io.hamal.lib.kua.extension.ScriptExtension
import io.hamal.lib.kua.extension.ScriptExtensionFactory
import io.hamal.runner.ctx.function.EmitFunction

class CtxExtensionFactory(
    private val eventCollector: MutableList<Event>
) : ScriptExtensionFactory {
    override fun create(): ScriptExtension {
        return ScriptExtension(
            name = "ctx",
            internals = mapOf(
                "emit" to EmitFunction(eventCollector)
            )
        )
    }
}