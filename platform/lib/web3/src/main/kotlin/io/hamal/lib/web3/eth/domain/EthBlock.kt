package io.hamal.lib.web3.eth.domain

import io.hamal.lib.web3.eth.abi.type.*


data class EthBlock(
    val baseFeePerGas: EthUint64?,
    val extraData: EthBytes32,
    val gasLimit: EthUint64,
    val gasUsed: EthUint64,
    val hash: EthHash?,
    val logsBloom: EthPrefixedHexString,
    val miner: EthAddress,
    var mixHash: EthHash,
    val number: EthUint64,
    val parentHash: EthHash,
    val receiptsRoot: EthHash,
    val sha3Uncles: EthHash,
    val size: EthUint64,
    val stateRoot: EthHash,
    val timestamp: EthUint64,
    val totalDifficulty: EthUint256,
    val transactions: List<EthTransaction>,
    val transactionsRoot: EthHash,
    val withdrawals: List<EthWithdrawal>,
    val withdrawalsRoot: EthHash,
)

data class EthLiteBlock(
    val number: EthUint64,
    val hash: EthHash,
    val parentHash: EthHash,
    val miner: EthAddress,
    val timestamp: EthUint64
)