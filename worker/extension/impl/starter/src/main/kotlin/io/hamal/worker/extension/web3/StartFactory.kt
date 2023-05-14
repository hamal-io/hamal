package io.hamal.worker.extension.web3

import io.hamal.worker.extension.api.WorkerExtensionFunction

class StartFactory : WorkerExtensionFunction.Factory {
    override fun create(): WorkerExtensionFunction = StartFunction()
}