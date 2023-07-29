package io.hamal.extension.web3.eth

import io.hamal.extension.web3.eth.abi.EthAbiExtensionFactory
import io.hamal.lib.kua.Extension
import io.hamal.lib.kua.ExtensionConfig
import io.hamal.lib.kua.ExtensionFactory
import io.hamal.lib.kua.function.NamedFunctionValue
import io.hamal.lib.kua.value.StringValue

class EthExtensionFactory : ExtensionFactory {

    override fun create(): Extension {
        val config = ExtensionConfig(
            mutableMapOf(
                "host" to StringValue("http://proxy:8000")
            )
        )
        return Extension(
            name = "eth",
            config = config,
            functions = listOf(
                NamedFunctionValue("get_block", GetBlockFunction(config)),
                NamedFunctionValue("call", CallFunction(config)),
                NamedFunctionValue("execute", ExecuteFunction(config))
            ),
            extensions = listOf(
                EthRequestExtensionFactory().create(),
                EthAbiExtensionFactory(config).create()
            )
        )
    }
}