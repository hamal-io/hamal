package io.hamal.extension.web3

import io.hamal.lib.kua.extension.NativeExtension
import io.hamal.lib.kua.extension.NativeExtensionFactory

class Web3ExtensionFactory : NativeExtensionFactory {

    override fun create(): NativeExtension {
        return NativeExtension(
            name = "web3",
            values = mapOf()
        )
    }

}