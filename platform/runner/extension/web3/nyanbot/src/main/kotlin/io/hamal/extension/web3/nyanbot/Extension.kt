package io.hamal.extension.web3.nyanbot

import io.hamal.lib.domain.vo.ExtensionName.Companion.ExtensionName
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.extension.RunnerExtension
import io.hamal.lib.kua.extend.extension.RunnerExtensionFactory


object ExtensionWeb3NyanbotFactory : RunnerExtensionFactory {
    override fun create(sandbox: Sandbox): RunnerExtension {
        return RunnerExtension(
            name = ExtensionName("web3.nyanbot"),
            nodeCompilers = listOf()
        )
    }
}
