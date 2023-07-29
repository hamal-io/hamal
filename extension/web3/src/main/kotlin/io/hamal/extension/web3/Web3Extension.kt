package io.hamal.extension.web3

import io.hamal.extension.web3.eth.EthExtensionFactory
import io.hamal.lib.kua.Extension
import io.hamal.lib.kua.ExtensionFactory

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