package io.hamal.extension.net.http

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extension.ExtensionConfig
import io.hamal.lib.kua.extension.ScriptExtension
import io.hamal.lib.kua.extension.ScriptExtensionFactory
import io.hamal.lib.kua.type.StringType

class HttpExtensionFactory : ScriptExtensionFactory {
    val config = ExtensionConfig(
        mutableMapOf(
            "host" to StringType("http://localhost:8000")
        )
    )

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