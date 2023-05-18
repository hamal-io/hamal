package io.hamal.agent.extension.web3

import io.hamal.agent.extension.api.Extension
import io.hamal.lib.script.api.Environment
import io.hamal.lib.script.api.NativeEnvironment
import io.hamal.lib.script.api.value.Identifier

class StartExtension : Extension {
    override fun create(): Environment {
        return NativeEnvironment(
            Identifier("Starter"),
            mapOf()
        )
    }
}