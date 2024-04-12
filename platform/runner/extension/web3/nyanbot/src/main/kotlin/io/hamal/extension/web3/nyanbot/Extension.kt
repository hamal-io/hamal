package io.hamal.extension.web3.nyanbot

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.extension.RunnerExtension
import io.hamal.lib.kua.extend.extension.RunnerExtensionFactory
import io.hamal.lib.kua.type.KuaString


object ExtensionWeb3NyanbotFactory : RunnerExtensionFactory {
    override fun create(sandbox: Sandbox): RunnerExtension {
        return RunnerExtension(
            name = KuaString("web3.nyanbot"),
            nodes = listOf()
        )
    }
}
