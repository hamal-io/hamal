package io.hamal.extension.web3.eth.abi

import io.hamal.lib.kua.Extension
import io.hamal.lib.kua.ExtensionConfig
import io.hamal.lib.kua.ExtensionFactory
import io.hamal.lib.kua.function.NamedFunctionValue

class EthAbiExtensionFactory(
    val config: ExtensionConfig
) : ExtensionFactory {
    override fun create(): Extension {
        return Extension(
            name = "abi",
            config = config,
            functions = listOf(
                NamedFunctionValue("decode_parameter", DecodeParameterFunction()),
            ),
        )
    }
}