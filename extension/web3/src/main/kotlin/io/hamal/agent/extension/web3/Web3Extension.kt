package io.hamal.agent.extension.web3

import io.hamal.agent.extension.api.ExtensionFactory
import io.hamal.agent.extension.web3.eth.EthExtensionFactory
import io.hamal.lib.kua.Extension

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