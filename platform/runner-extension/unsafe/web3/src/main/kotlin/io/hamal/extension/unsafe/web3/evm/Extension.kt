package io.hamal.extension.unsafe.web3.evm

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extension.ExtensionConfig
import io.hamal.lib.kua.extension.unsafe.RunnerUnsafeExtension
import io.hamal.lib.kua.extension.unsafe.RunnerUnsafeExtensionFactory
import io.hamal.lib.kua.type.StringType

class EthExtensionFactory : RunnerUnsafeExtensionFactory {
    val config = ExtensionConfig(
        mutableMapOf(
            "host" to StringType("http://localhost:8000")
        )
    )

    override fun create(sandbox: Sandbox): RunnerUnsafeExtension {
        return RunnerUnsafeExtension(
            name = "web3.eth",
            internals = mapOf(
                "execute" to EthExecuteFunction(config),
                "decode_parameter" to EthDecodeParameterFunction
            ),
            config = config
        )
    }
}
