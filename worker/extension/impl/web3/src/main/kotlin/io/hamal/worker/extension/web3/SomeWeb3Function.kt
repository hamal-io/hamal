package io.hamal.worker.extension.web3

import io.hamal.worker.extension.api.WorkerExtensionFunction

class SomeWeb3Function : WorkerExtensionFunction {
    override fun invoke() {
        println("Hello World from Web3")
    }
}