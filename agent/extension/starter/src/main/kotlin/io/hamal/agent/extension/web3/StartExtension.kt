package io.hamal.agent.extension.web3

import io.hamal.agent.extension.api.Extension
import io.hamal.lib.common.value.EnvValue
import io.hamal.lib.common.value.IdentValue

class StartExtension : Extension {
    override fun create(): EnvValue {
        return EnvValue(
            IdentValue("Starter"),
            mapOf()
        )
    }
}