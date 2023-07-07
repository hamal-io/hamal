package io.hamal.lib.web3.eth.abi


sealed interface EthInputTuple {
    fun plainSignature(): String?
}

object EthInputTuple0 : EthInputTuple {
    override fun plainSignature(): String {
        return "()"
    }
}


