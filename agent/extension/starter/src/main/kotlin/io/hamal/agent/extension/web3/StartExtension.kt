package io.hamal.agent.extension.web3

import io.hamal.agent.extension.api.Extension
import io.hamal.lib.script.api.value.DepEnvironmentValue
import io.hamal.lib.script.api.value.DepIdentifier

class StartExtension : Extension {
    override fun create(): DepEnvironmentValue {
        return DepEnvironmentValue(
            DepIdentifier("Starter"),
            mapOf()
        )
    }
}