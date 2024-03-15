package io.hamal.app.web3proxy.eth.repository

import com.google.gson.annotations.SerializedName
import io.hamal.lib.web3.eth.abi.type.*

internal data class BlockEntity(
    @SerializedName("a")
    val baseFeePerGas: EthUint64?,
    @SerializedName("b")
    val extraData: EthBytes32,
    @SerializedName("c")
    val gasLimit: EthUint64,
    @SerializedName("d")
    val gasUsed: EthUint64,
    @SerializedName("e")
    val hash: EthHash,
    @SerializedName("f")
    val logsBloom: EthPrefixedHexString,
    @SerializedName("g")
    val miner: EthAddressId,
    @SerializedName("h")
    var mixHash: EthHash,
    @SerializedName("i")
    val number: EthUint64,
    @SerializedName("j")
    val parentHash: EthHash,
    @SerializedName("k")
    val receiptsRoot: EthHash,
    @SerializedName("l")
    val sha3Uncles: EthHash,
    @SerializedName("m")
    val size: EthUint64,
    @SerializedName("n")
    val stateRoot: EthHash,
    @SerializedName("o")
    val timestamp: EthUint64,
    @SerializedName("p")
    val totalDifficulty: EthUint256,
    @SerializedName("q")
    val transactions: List<TransactionEntity>,
    @SerializedName("r")
    val transactionsRoot: EthHash,
    @SerializedName("s")
    val withdrawals: List<WithdrawalEntity>?,
    @SerializedName("t")
    val withdrawalsRoot: EthHash?,
)

internal data class TransactionEntity(
    @SerializedName("a")
    val from: EthAddressId,
    @SerializedName("b")
    val gas: EthUint64,
    @SerializedName("c")
    val gasPrice: EthUint64,
    @SerializedName("d")
    val maxPriorityFeePerGas: EthUint64?,
    @SerializedName("e")
    val maxFeePerGas: EthUint64?,
    @SerializedName("f")
    val hash: EthHash,
    @SerializedName("g")
    val input: EthPrefixedHexString?,
    @SerializedName("h")
    val nonce: EthUint64,
    @SerializedName("i")
    val to: EthAddressId?,
    @SerializedName("j")
    val value: EthUint256?,
    @SerializedName("k")
    val type: EthUint8,
    @SerializedName("l")
    val accessList: List<AccessListItem>?
) {
    data class AccessListItem(
        @SerializedName("a")
        val address: EthAddressId,
        @SerializedName("b")
        val storageKeys: List<EthHash>
    )
}

internal data class WithdrawalEntity(
    @SerializedName("a")
    val index: EthUint64,
    @SerializedName("b")
    val validatorIndex: EthUint64,
    @SerializedName("c")
    val address: EthAddressId,
    @SerializedName("d")
    val amount: EthUint64,
)


@JvmInline
value class EthAddressId(val value: Long)
