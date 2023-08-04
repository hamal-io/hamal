package io.hamal.lib.web3.hml.domain

import io.hamal.lib.web3.eth.abi.type.*
import kotlinx.serialization.Serializable

@Serializable
data class HmlBlock(
    val number: EthUint64,
    val hash: EthHash,
    val miner: EthAddress,
    val minerId: EthUint64,
    val gasLimit: EthUint64,
    val gasUsed: EthUint64,
    val timestamp: EthUint64,
    val transactions: List<Transaction>
) {
    @Serializable
    data class Transaction(
        val blockIndex: EthUint32,
        val type: EthUint8,
        val from: EthAddress,
        val fromId: EthUint64,
        val to: EthAddress?,
        val toId: EthUint64,
        val input: EthPrefixedHexString,
        val value: EthUint256,
        val gas: EthUint64,
    )
}
