package io.hamal.extension.unsafe.net.http

import io.hamal.extension.unsafe.net.http.function.HttpExecuteFunction
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extension.ExtensionConfig
import io.hamal.lib.kua.extension.unsafe.RunnerUnsafeExtension
import io.hamal.lib.kua.extension.unsafe.RunnerUnsafeExtensionFactory

class HttpExtensionFactory(
    val config: ExtensionConfig = ExtensionConfig(mutableMapOf())
) : RunnerUnsafeExtensionFactory {


    override fun create(sandbox: Sandbox): RunnerUnsafeExtension {
        return RunnerUnsafeExtension(
            name = "net.http",
            internals = mapOf(
                "execute" to HttpExecuteFunction()
            ),
            config = config
        )
    }
}