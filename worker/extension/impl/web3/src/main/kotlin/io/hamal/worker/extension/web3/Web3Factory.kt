package io.hamal.worker.extension.web3

import io.hamal.worker.extension.api.WorkerExtensionFunction
import io.hamal.worker.extension.web3.eth.EthGetBlock

class Web3Factory : WorkerExtensionFunction.Factory {
    override fun create(): WorkerExtensionFunction = EthGetBlock()
}