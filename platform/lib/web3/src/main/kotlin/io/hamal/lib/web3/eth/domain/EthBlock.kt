package io.hamal.lib.web3.eth.domain

import io.hamal.lib.web3.eth.abi.type.*


data class EthBlock(
    val number: EthUint64?,
    val hash: EthHash?,
    val parentHash: EthHash?,
    val sha3Uncles: EthHash?,
    val miner: EthAddress?,
    val stateRoot: EthHash?,
    val transactionsRoot: EthHash?,
    val receiptsRoot: EthHash?,
    val gasLimit: EthUint64?,
    val gasUsed: EthUint64?,
    val timestamp: EthUint64?,
//    val extraData: EthBytes32,
    val transactions: List<Transaction>?
) {
    data class Transaction(
        val type: EthUint8?,
        val hash: EthHash?,
        val from: EthAddress?,
        val to: EthAddress?,
        val input: EthPrefixedHexString?,
        val value: EthUint256?,
        val gas: EthUint64?,
        val gasPrice: EthUint64?,
    )
}

data class EthLiteBlock(
    val number: EthUint64,
    val hash: EthHash,
    val parentHash: EthHash,
    val miner: EthAddress,
    val timestamp: EthUint64
)