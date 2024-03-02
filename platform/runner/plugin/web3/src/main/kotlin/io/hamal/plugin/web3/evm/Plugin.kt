package io.hamal.plugin.web3.evm

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.extend.plugin.RunnerPluginFactory
import io.hamal.lib.kua.type.KuaString

class PluginWeb3EthFactory : RunnerPluginFactory {
    override fun create(sandbox: Sandbox): RunnerPlugin {
        return RunnerPlugin(
            name = KuaString("web3.eth"),
            internals = mapOf(
                KuaString("execute") to EthExecuteFunction(),
                KuaString("decode_parameter") to EthDecodeParameterFunction
            )
        )
    }
}
