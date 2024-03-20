package io.hamal.lib.web3.evm.domain

import io.hamal.lib.web3.evm.abi.type.EvmHash
import io.hamal.lib.web3.evm.abi.type.EvmUint64

interface EvmBlock {
    val number: EvmUint64
    val hash: EvmHash
}

data class EvmBlockParameter(val value: String) {

    constructor(blockNum: Long) : this(
        ("0x" + java.lang.Long.toHexString(blockNum)).also {
            require(blockNum >= 0) { "Block number must not be negative" }
        }
    )

    companion object {
        val Latest = EvmBlockParameter("latest")
        val Earliest = EvmBlockParameter("earliest")
        val Pending = EvmBlockParameter("pending")
    }
}

