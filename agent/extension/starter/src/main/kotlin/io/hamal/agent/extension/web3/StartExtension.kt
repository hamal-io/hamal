package io.hamal.agent.extension.web3

import io.hamal.agent.extension.api.Extension
import io.hamal.lib.script.api.value.EnvValue
import io.hamal.lib.script.api.value.IdentValue
import io.hamal.lib.script.api.value.TableValue

class StartExtension : Extension {
    override fun create(): EnvValue {
        return EnvValue(
            IdentValue("Starter"),
            TableValue()
        )
    }
}