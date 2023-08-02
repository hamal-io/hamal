package io.hamal.extension.web3.eth

import io.hamal.lib.kua.extension.ExtensionConfig
import io.hamal.lib.kua.extension.NativeExtension
import io.hamal.lib.kua.extension.NativeExtensionFactory
import io.hamal.lib.kua.value.StringValue

class EthExtensionFactory : NativeExtensionFactory {

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