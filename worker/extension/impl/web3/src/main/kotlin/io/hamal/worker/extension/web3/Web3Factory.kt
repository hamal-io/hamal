package io.hamal.worker.extension.web3

import io.hamal.worker.extension.api.WorkerExtensionFunction

class Web3Factory : WorkerExtensionFunction.Factory {
    override fun invoke(): WorkerExtensionFunction = SomeWeb3Function()
}