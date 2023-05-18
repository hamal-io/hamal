package io.hamal.agent.extension.web3

import io.hamal.agent.extension.api.Extension
import io.hamal.lib.script.api.value.EnvironmentValue
import io.hamal.lib.script.api.value.Identifier

class StartExtension : Extension {
    override fun create(): EnvironmentValue {
        return EnvironmentValue(
            Identifier("Starter"),
            mapOf()
        )
    }
}