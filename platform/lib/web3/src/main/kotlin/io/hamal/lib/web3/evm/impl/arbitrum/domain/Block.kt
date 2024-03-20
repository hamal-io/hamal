package io.hamal.lib.web3.evm.impl.arbitrum.domain

import io.hamal.lib.web3.evm.abi.type.*
import io.hamal.lib.web3.evm.domain.EvmBlock


data class ArbitrumBlock(
    val baseFeePerGas: EvmUint64?,
    val extraData: EvmBytes32,
    val gasLimit: EvmUint64,
    val gasUsed: EvmUint64,
    override val hash: EvmHash,
    val l1BlockNumber: EvmUint64,
    val logsBloom: EvmPrefixedHexString,
    val miner: EvmAddress,
    var mixHash: EvmHash,
    override val number: EvmUint64,
    val sendCount: EvmUint64,
    val sendRoot: EvmHash,
    val parentHash: EvmHash,
    val receiptsRoot: EvmHash,
    val sha3Uncles: EvmHash,
    val size: EvmUint64,
    val stateRoot: EvmHash,
    val timestamp: EvmUint64,
    val totalDifficulty: EvmUint256,
    val transactions: List<ArbitrumTransaction>,
    val transactionsRoot: EvmHash,
) : EvmBlock
