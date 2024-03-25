package io.hamal.app.web3proxy.arbitrum.repository

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
    val l1BlockNumber: EvmUint64,
    @SerializedName("g")
    val logsBloom: EvmPrefixedHexString,
    @SerializedName("h")
    val miner: ArbitrumAddressId,
    @SerializedName("i")
    var mixHash: EvmHash,
    @SerializedName("j")
    val number: EvmUint64,
    @SerializedName("k")
    val sendCount: EvmUint64?,
    @SerializedName("l")
    val sendRoot: EvmHash?,
    @SerializedName("m")
    val parentHash: EvmHash,
    @SerializedName("n")
    val receiptsRoot: EvmHash,
    @SerializedName("o")
    val sha3Uncles: EvmHash,
    @SerializedName("p")
    val size: EvmUint64,
    @SerializedName("q")
    val stateRoot: EvmHash,
    @SerializedName("r")
    val timestamp: EvmUint64,
    @SerializedName("s")
    val totalDifficulty: EvmUint256,
    @SerializedName("t")
    val transactions: List<TransactionEntity>,
    @SerializedName("u")
    val transactionsRoot: EvmHash,
)

internal data class TransactionEntity(
    @SerializedName("a")
    val from: ArbitrumAddressId,
    @SerializedName("b")
    val gas: EvmUint64,
    @SerializedName("c")
    val gasPrice: EvmUint64,
    @SerializedName("d")
    val hash: EvmHash,
    @SerializedName("e")
    val input: EvmPrefixedHexString?,
    @SerializedName("f")
    val nonce: EvmUint64,
    @SerializedName("g")
    val to: ArbitrumAddressId?,
    @SerializedName("h")
    val value: EvmUint256?,
    @SerializedName("i")
    val type: EvmUint8,
    @SerializedName("j")
    val requestId: EvmUint64?
)

@JvmInline
value class ArbitrumAddressId(val value: Long)
