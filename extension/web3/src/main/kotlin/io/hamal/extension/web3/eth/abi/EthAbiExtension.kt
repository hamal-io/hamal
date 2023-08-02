package io.hamal.extension.web3.eth.abi

import io.hamal.lib.kua.extension.Extension
import io.hamal.lib.kua.extension.ExtensionConfig
import io.hamal.lib.kua.extension.ExtensionFactory
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