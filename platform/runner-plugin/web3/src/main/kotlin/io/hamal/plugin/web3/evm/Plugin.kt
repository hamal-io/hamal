package io.hamal.plugin.web3.evm

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.extend.plugin.RunnerPluginFactory

class PluginWeb3EthFactory : RunnerPluginFactory {
    override fun create(sandbox: Sandbox): RunnerPlugin {
        return RunnerPlugin(
            name = "web3.eth",
            internals = mapOf(
                "execute" to EthExecuteFunction(),
                "decode_parameter" to EthDecodeParameterFunction
            )
        )
    }
}
