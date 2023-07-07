package io.hamal.lib.web3.eth.abi

import io.hamal.lib.web3.eth.abi.type.EthHash
import io.hamal.lib.web3.eth.abi.type.EthPrefixedHexString
import io.hamal.lib.web3.util.Web3Formatter
import io.hamal.lib.web3.util.Web3HashUtils


data class EthEventSignature(
    val value: String
) {
    val encoded by lazy {
        val hash: ByteArray = Web3HashUtils.keccak256(value.toByteArray())
        EthHash(EthPrefixedHexString("0x" + Web3Formatter.formatToHex(hash)))
    }

    override fun toString(): String {
        return encoded.toString()
    }
}