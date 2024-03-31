package io.hamal.lib.web3.evm.chain.eth.domain

import io.hamal.lib.web3.evm.abi.type.*
import io.hamal.lib.web3.evm.domain.EvmBlockData


data class EthBlockData(
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
    val transactions: List<EthTransactionData>,
    val transactionsRoot: EvmHash,
    val withdrawals: List<EthWithdrawalData>?,
    val withdrawalsRoot: EvmHash?,
) : EvmBlockData