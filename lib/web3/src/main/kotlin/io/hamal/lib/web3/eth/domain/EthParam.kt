package io.hamal.lib.web3.eth.domain

import java.lang.Long.toHexString


data class EthBlockParam(val value: String) {
    constructor(blockNum: Long) : this(
        ("0x" + toHexString(blockNum)).also {
            require(blockNum >= 0) { "Block number must not be negative" }
        }
    )

    companion object {
        val Latest = EthBlockParam("latest")
        val Earliest = EthBlockParam("earliest")
        val Pending = EthBlockParam("pending")
    }
}

