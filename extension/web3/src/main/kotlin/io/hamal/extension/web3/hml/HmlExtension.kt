package io.hamal.extension.web3.hml

import io.hamal.lib.kua.extension.ExtensionConfig
import io.hamal.lib.kua.extension.ScriptExtension
import io.hamal.lib.kua.extension.ScriptExtensionFactory
import io.hamal.lib.kua.value.StringValue

class HmlExtensionFactory : ScriptExtensionFactory {
    val config = ExtensionConfig(
        mutableMapOf(
            "host" to StringValue("http://proxy:8000")
        )
    )

    override fun create(): ScriptExtension {
        return ScriptExtension(
            name = "web3.hml",
            config = config,
            internals = mapOf(
                "execute" to ExecuteFunction(config),
            )
        )
    }
}
