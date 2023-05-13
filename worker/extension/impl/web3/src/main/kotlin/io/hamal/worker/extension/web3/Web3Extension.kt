package io.hamal.worker.extension.web3

import io.hamal.worker.extension.api.WorkerExtension
import io.hamal.worker.extension.api.WorkerExtensionFunction

class Web3Extension : WorkerExtension {
    override fun functionFactories(): List<WorkerExtensionFunction.Factory> {
        return listOf(Web3Factory())
    }
}