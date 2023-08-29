package io.hamal.extension.net.http

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extension.ExtensionConfig
import io.hamal.lib.kua.extension.ScriptExtension
import io.hamal.lib.kua.extension.ScriptExtensionFactory

class HttpExtensionFactory(
    val config: ExtensionConfig = ExtensionConfig(mutableMapOf())
) : ScriptExtensionFactory {


    override fun create(sandbox: Sandbox): ScriptExtension {
        return ScriptExtension(
            name = "net.http",
            config = config,
            internals = mapOf(
                "execute" to HttpExecuteFunction()
            )
        )
    }
}