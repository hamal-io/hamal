package io.hamal.plugin.net.http

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.extend.plugin.RunnerPluginFactory
import io.hamal.lib.value.ValueString
import io.hamal.plugin.net.http.function.HttpExecuteFunction

class PluginHttpFactory : RunnerPluginFactory {
    override fun create(sandbox: Sandbox): RunnerPlugin {
        return RunnerPlugin(
            name = ValueString("net.http"),
            internals = mapOf(
                ValueString("execute") to HttpExecuteFunction()
            )
        )
    }
}