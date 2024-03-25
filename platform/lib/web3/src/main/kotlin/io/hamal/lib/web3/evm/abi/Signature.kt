package io.hamal.lib.web3.evm.abi

import io.hamal.lib.web3.evm.abi.type.EvmHash
import io.hamal.lib.web3.evm.abi.type.EvmPrefixedHexString
import io.hamal.lib.web3.util.Web3Formatter
import io.hamal.lib.web3.util.Web3HashUtils


data class EvmSignature(
    val value: String
) {
    val encoded by lazy {
        val hash: ByteArray = Web3HashUtils.keccak256(value.toByteArray())
        EvmHash(EvmPrefixedHexString("0x" + Web3Formatter.formatToHex(hash)))
    }

    override fun toString(): String {
        return encoded.toString()
    }
}