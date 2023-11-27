package io.hamal.plugin.web3.evm

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.ExtensionConfig
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.extend.plugin.RunnerPluginFactory
import io.hamal.lib.kua.type.StringType

class EthPluginFactory : RunnerPluginFactory {
    val config = ExtensionConfig(
        mutableMapOf(
            "host" to StringType("http://localhost:8000")
        )
    )

    override fun create(sandbox: Sandbox): RunnerPlugin {
        return RunnerPlugin(
            name = "web3.eth",
            internals = mapOf(
                "execute" to EthExecuteFunction(config),
                "decode_parameter" to EthDecodeParameterFunction
            ),
            config = config
        )
    }
}
