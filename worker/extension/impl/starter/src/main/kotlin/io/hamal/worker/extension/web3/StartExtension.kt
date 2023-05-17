package io.hamal.worker.extension.web3

import io.hamal.lib.script.api.Environment
import io.hamal.lib.script.api.native_.FunctionValue
import io.hamal.worker.extension.api.WorkerExtension

class StartExtension : WorkerExtension {
    override fun functionFactories(): List<FunctionValue> {
        return listOf()
    }

    override fun environments(): List<Environment> {
        return listOf()
    }
}