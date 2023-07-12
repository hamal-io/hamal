package io.hamal.lib.web3.eth.domain

import io.hamal.lib.web3.eth.abi.type.*
import kotlinx.serialization.Serializable

@Serializable
data class EthTransaction(
    val type: EthUint8,
    val blockNumber: EthUint64,
    val blockHash: EthHash,
    val hash: EthHash,
    val from: EthAddress,
    val to: EthAddress,
    val input: EthPrefixedHexString,
    val value: EthUint256,
    val gas: EthUint64,
    val gasPrice: EthUint64,
)

