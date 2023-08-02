package io.hamal.extension.web3

import io.hamal.extension.web3.eth.EthExtensionFactory
import io.hamal.lib.kua.extension.Extension
import io.hamal.lib.kua.extension.ExtensionFactory

class Web3ExtensionFactory : ExtensionFactory {

    override fun create(): Extension {
        return Extension(
            name = "web3",
            functions = listOf(),
            extensions = listOf(
                EthExtensionFactory().create()
            )
        )
    }

}