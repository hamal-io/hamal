package io.hamal.plugin.net.http

import io.hamal.plugin.net.http.function.HttpExecuteFunction
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extension.ExtensionConfig
import io.hamal.lib.kua.extension.plugin.RunnerPluginExtension
import io.hamal.lib.kua.extension.plugin.RunnerPluginExtensionFactory

class HttpPluginFactory(
    val config: ExtensionConfig = ExtensionConfig(mutableMapOf())
) : RunnerPluginExtensionFactory {


    override fun create(sandbox: Sandbox): RunnerPluginExtension {
        return RunnerPluginExtension(
            name = "net.http",
            internals = mapOf(
                "execute" to HttpExecuteFunction()
            ),
            config = config
        )
    }
}