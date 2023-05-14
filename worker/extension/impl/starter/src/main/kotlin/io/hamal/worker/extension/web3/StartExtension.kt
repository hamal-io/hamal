package io.hamal.worker.extension.web3

import io.hamal.lib.script.api.native_.NativeFunction
import io.hamal.worker.extension.api.WorkerExtension

class StartExtension : WorkerExtension {
    override fun functionFactories(): List<NativeFunction> {
        return listOf()
    }
}