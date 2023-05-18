package io.hamal.agent.extension.web3

import io.hamal.lib.script.api.Environment
import io.hamal.lib.script.api.native_.FunctionValue
import io.hamal.agent.extension.api.Extension

class StartExtension : Extension {
    override fun functionFactories(): List<FunctionValue> {
        return listOf()
    }

    override fun environments(): List<Environment> {
        return listOf()
    }
}