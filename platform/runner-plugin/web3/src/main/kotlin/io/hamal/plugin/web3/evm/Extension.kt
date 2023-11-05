package io.hamal.plugin.web3.evm

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extension.ExtensionConfig
import io.hamal.lib.kua.extension.plugin.RunnerPluginExtension
import io.hamal.lib.kua.extension.plugin.RunnerPluginExtensionFactory
import io.hamal.lib.kua.type.StringType

class EthPluginFactory : RunnerPluginExtensionFactory {
    val config = ExtensionConfig(
        mutableMapOf(
            "host" to StringType("http://localhost:8000")
        )
    )

    override fun create(sandbox: Sandbox): RunnerPluginExtension {
        return RunnerPluginExtension(
            name = "web3.eth",
            internals = mapOf(
                "execute" to EthExecuteFunction(config),
                "decode_parameter" to EthDecodeParameterFunction
            ),
            config = config
        )
    }
}
