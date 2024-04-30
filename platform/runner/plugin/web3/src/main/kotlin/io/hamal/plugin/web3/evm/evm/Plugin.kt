package io.hamal.plugin.web3.evm.evm

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.extend.plugin.RunnerPluginFactory
import io.hamal.lib.value.ValueString

class PluginWeb3EvmFactory : RunnerPluginFactory {
    override fun create(sandbox: Sandbox): RunnerPlugin {
        return RunnerPlugin(
            name = ValueString("web3.evm"),
            internals = mapOf(
                ValueString("execute") to EvmExecuteFunction(),
                ValueString("decode_parameter") to EvmDecodeParameterFunction
            )
        )
    }
}
