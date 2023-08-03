package io.hamal.extension.web3.eth

import io.hamal.lib.kua.extension.*
import io.hamal.lib.kua.value.StringValue

class DepEthExtensionFactory : NativeExtensionFactory {

    override fun create(): NativeExtension {
        val config = ExtensionConfig(
            mutableMapOf(
                "host" to StringValue("http://proxy:8000")
            )
        )
        return NativeExtension(
            name = "eth",
            config = config,
            values = mapOf()
        )
    }
}

class EthExtensionFactory : ScriptExtensionFactory {
    val config = ExtensionConfig(
        mutableMapOf(
            "host" to StringValue("http://proxy:8000")
        )
    )

    override fun create(): ScriptExtension {
        return ScriptExtension(
            name = "web3/eth",
            config = config,
            internals = mapOf(
                "execute" to ExecuteFunction(config)
            ),
            init = ScriptExtension.loadInitFromResources("eth_extension.lua")
        )
    }
}
