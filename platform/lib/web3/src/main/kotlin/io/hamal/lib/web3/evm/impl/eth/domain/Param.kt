package io.hamal.lib.web3.evm.impl.eth.domain

import java.lang.Long.toHexString

data class EthBlockParameter(val value: String) {

    constructor(blockNum: Long) : this(
        ("0x" + toHexString(blockNum)).also {
            require(blockNum >= 0) { "Block number must not be negative" }
        }
    )

    companion object {
        val Latest = EthBlockParameter("latest")
        val Earliest = EthBlockParameter("earliest")
        val Pending = EthBlockParameter("pending")
    }
}

