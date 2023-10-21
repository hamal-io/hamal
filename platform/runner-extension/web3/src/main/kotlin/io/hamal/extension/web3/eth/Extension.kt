package io.hamal.extension.web3.eth

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extension.ExtensionConfig
import io.hamal.lib.kua.extension.Capability
import io.hamal.lib.kua.extension.CapabilityFactory
import io.hamal.lib.kua.type.StringType

class EthExtensionFactory : CapabilityFactory {
    val config = ExtensionConfig(
        mutableMapOf(
            "host" to StringType("http://localhost:8000")
        )
    )

    override fun create(sandbox: Sandbox): Capability {
        return Capability(
            name = "web3.eth",
            config = config,
            internals = mapOf(
                "execute" to EthExecuteFunction(config),
                "decode_parameter" to EthDecodeParameterFunction
            )
        )
    }
}
