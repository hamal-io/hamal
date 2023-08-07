package io.hamal.extension.web3.eth

import io.hamal.lib.kua.extension.ExtensionConfig
import io.hamal.lib.kua.extension.ScriptExtension
import io.hamal.lib.kua.extension.ScriptExtensionFactory
import io.hamal.lib.kua.value.StringValue

class EthExtensionFactory : ScriptExtensionFactory {
    val config = ExtensionConfig(
        mutableMapOf(
            "host" to StringValue("http://localhost:8000")
        )
    )

    override fun create(): ScriptExtension {
        return ScriptExtension(
            name = "web3.eth",
            config = config,
            internals = mapOf(
                "execute" to ExecuteFunction(config),
                "decode_parameter" to DecodeParameterFunction
            )
        )
    }
}
