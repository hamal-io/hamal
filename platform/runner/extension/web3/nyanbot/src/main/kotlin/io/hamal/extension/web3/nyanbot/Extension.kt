package io.hamal.extension.web3.nyanbot

import io.hamal.lib.common.value.ValueString
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.extension.RunnerExtension
import io.hamal.lib.kua.extend.extension.RunnerExtensionFactory


object ExtensionWeb3NyanbotFactory : RunnerExtensionFactory {
    override fun create(sandbox: Sandbox): RunnerExtension {
        return RunnerExtension(
            name = ValueString("web3.nyanbot"),
            nodeCompilers = listOf()
        )
    }
}
