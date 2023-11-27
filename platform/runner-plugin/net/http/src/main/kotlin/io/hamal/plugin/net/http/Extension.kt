package io.hamal.plugin.net.http

import io.hamal.plugin.net.http.function.HttpExecuteFunction
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.ExtensionConfig
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.extend.plugin.RunnerPluginFactory

class HttpPluginFactory(
    val config: ExtensionConfig = ExtensionConfig(mutableMapOf())
) : RunnerPluginFactory {


    override fun create(sandbox: Sandbox): RunnerPlugin {
        return RunnerPlugin(
            name = "net.http",
            internals = mapOf(
                "execute" to HttpExecuteFunction()
            ),
            config = config
        )
    }
}