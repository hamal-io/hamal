package io.hamal.extension.web3.nyanbot

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.extension.RunnerExtension
import io.hamal.lib.kua.extend.extension.RunnerExtensionFactory
import io.hamal.lib.value.ValueString


object ExtensionWeb3NyanbotFactory : RunnerExtensionFactory {
    override fun create(sandbox: Sandbox): RunnerExtension {
        return RunnerExtension(
            name = ValueString("web3.nyanbot"),
            nodes = listOf()
        )
    }
}
