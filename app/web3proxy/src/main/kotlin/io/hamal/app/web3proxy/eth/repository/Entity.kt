package io.hamal.app.web3proxy.eth.repository

import io.hamal.lib.web3.eth.abi.type.*

internal data class BlockEntity(
    val baseFeePerGas: EthUint64?,
    val extraData: EthBytes32,
    val gasLimit: EthUint64,
    val gasUsed: EthUint64,
    val hash: EthHash,
    val logsBloom: EthPrefixedHexString,
    val miner: EthAddressId,
    var mixHash: EthHash,
    val number: EthUint64,
    val parentHash: EthHash,
    val receiptsRoot: EthHash,
    val sha3Uncles: EthHash,
    val size: EthUint64,
    val stateRoot: EthHash,
    val timestamp: EthUint64,
    val totalDifficulty: EthUint256,
    val transactions: List<TransactionEntity>,
    val transactionsRoot: EthHash,
    val withdrawals: List<WithdrawalEntity>?,
    val withdrawalsRoot: EthHash?,
)

internal data class TransactionEntity(
    val from: EthAddressId?,
    val gas: EthUint64?,
    val gasPrice: EthUint64?,
    val maxPriorityFeePerGas: EthUint64?,
    val maxFeePerGas: EthUint64?,
    val hash: EthHash?,
    val input: EthPrefixedHexString?,
    val nonce: EthUint64?,
    val to: EthAddressId?,
    val value: EthUint256?,
    val type: EthUint8?,
    val accessList: List<AccessListItem>?
) {
    data class AccessListItem(
        val address: EthAddressId,
        val storageKeys: List<EthHash>
    )
}

internal data class WithdrawalEntity(
    val index: EthUint64,
    val validatorIndex: EthUint64,
    val address: EthAddressId,
    val amount: EthUint64,
)


@JvmInline
value class EthAddressId(val value: Long)
