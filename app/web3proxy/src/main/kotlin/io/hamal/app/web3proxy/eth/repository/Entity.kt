package io.hamal.app.web3proxy.eth.repository

import com.google.gson.annotations.SerializedName
import io.hamal.lib.web3.evm.abi.type.*

internal data class BlockEntity(
    @SerializedName("a")
    val baseFeePerGas: EvmUint64?,
    @SerializedName("b")
    val extraData: EvmBytes32,
    @SerializedName("c")
    val gasLimit: EvmUint64,
    @SerializedName("d")
    val gasUsed: EvmUint64,
    @SerializedName("e")
    val hash: EvmHash,
    @SerializedName("f")
    val logsBloom: EvmPrefixedHexString,
    @SerializedName("g")
    val miner: EthAddressId,
    @SerializedName("h")
    var mixHash: EvmHash,
    @SerializedName("i")
    val number: EvmUint64,
    @SerializedName("j")
    val parentHash: EvmHash,
    @SerializedName("k")
    val receiptsRoot: EvmHash,
    @SerializedName("l")
    val sha3Uncles: EvmHash,
    @SerializedName("m")
    val size: EvmUint64,
    @SerializedName("n")
    val stateRoot: EvmHash,
    @SerializedName("o")
    val timestamp: EvmUint64,
    @SerializedName("p")
    val totalDifficulty: EvmUint256,
    @SerializedName("q")
    val transactions: List<TransactionEntity>,
    @SerializedName("r")
    val transactionsRoot: EvmHash,
    @SerializedName("s")
    val withdrawals: List<WithdrawalEntity>?,
    @SerializedName("t")
    val withdrawalsRoot: EvmHash?,
)

internal data class TransactionEntity(
    @SerializedName("a")
    val from: EthAddressId,
    @SerializedName("b")
    val gas: EvmUint64,
    @SerializedName("c")
    val gasPrice: EvmUint64,
    @SerializedName("d")
    val maxPriorityFeePerGas: EvmUint64?,
    @SerializedName("e")
    val maxFeePerGas: EvmUint64?,
    @SerializedName("f")
    val hash: EvmHash,
    @SerializedName("g")
    val input: EvmPrefixedHexString?,
    @SerializedName("h")
    val nonce: EvmUint64,
    @SerializedName("i")
    val to: EthAddressId?,
    @SerializedName("j")
    val value: EvmUint256?,
    @SerializedName("k")
    val type: EvmUint8,
    @SerializedName("l")
    val accessList: List<AccessListItem>?
) {
    data class AccessListItem(
        @SerializedName("a")
        val address: EthAddressId,
        @SerializedName("b")
        val storageKeys: List<EvmHash>
    )
}

internal data class WithdrawalEntity(
    @SerializedName("a")
    val index: EvmUint64,
    @SerializedName("b")
    val validatorIndex: EvmUint64,
    @SerializedName("c")
    val address: EthAddressId,
    @SerializedName("d")
    val amount: EvmUint64,
)


@JvmInline
value class EthAddressId(val value: Long)
