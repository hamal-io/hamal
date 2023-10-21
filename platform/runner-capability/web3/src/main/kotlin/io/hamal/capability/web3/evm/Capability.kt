package io.hamal.capability.web3.evm

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.plugin.capability.Capability
import io.hamal.lib.kua.plugin.capability.CapabilityConfig
import io.hamal.lib.kua.plugin.capability.CapabilityFactory
import io.hamal.lib.kua.type.StringType

class EthCapabilityFactory : CapabilityFactory {
    val config = CapabilityConfig(
        mutableMapOf(
            "host" to StringType("http://localhost:8000")
        )
    )

    override fun create(sandbox: Sandbox): Capability {
        return Capability(
            name = "web3.eth",
            internals = mapOf(
                "execute" to EthExecuteFunction(config),
                "decode_parameter" to EthDecodeParameterFunction
            ),
            config = config
        )
    }
}
