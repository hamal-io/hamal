package io.hamal.extension.web3.eth.abi

import io.hamal.lib.kua.extension.ExtensionConfig
import io.hamal.lib.kua.extension.NativeExtension
import io.hamal.lib.kua.extension.NativeExtensionFactory

class EthAbiExtensionFactory(
    val config: ExtensionConfig
) : NativeExtensionFactory {
    override fun create(): NativeExtension {
        return NativeExtension(
            name = "abi",
            config = config,
            values = mapOf()
        )
    }
}