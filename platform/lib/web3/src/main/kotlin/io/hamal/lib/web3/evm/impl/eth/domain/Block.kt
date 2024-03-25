package io.hamal.lib.web3.evm.impl.eth.domain

import io.hamal.lib.web3.evm.abi.type.*
import io.hamal.lib.web3.evm.domain.EvmBlock


data class EthBlock(
    val baseFeePerGas: EvmUint64?,
    val extraData: EvmBytes32,
    val gasLimit: EvmUint64,
    val gasUsed: EvmUint64,
    override val hash: EvmHash,
    val logsBloom: EvmPrefixedHexString,
    val miner: EvmAddress,
    var mixHash: EvmHash,
    override val number: EvmUint64,
    val parentHash: EvmHash,
    val receiptsRoot: EvmHash,
    val sha3Uncles: EvmHash,
    val size: EvmUint64,
    val stateRoot: EvmHash,
    val timestamp: EvmUint64,
    val totalDifficulty: EvmUint256,
    val transactions: List<EthTransaction>,
    val transactionsRoot: EvmHash,
    val withdrawals: List<Withdrawal>?,
    val withdrawalsRoot: EvmHash?,
) : EvmBlock

data class EthLiteBlock(
    val number: EvmUint64,
    val hash: EvmHash,
    val parentHash: EvmHash,
    val miner: EvmAddress,
    val timestamp: EvmUint64
)