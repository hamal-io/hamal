package io.hamal.lib.web3.eth.domain

import io.hamal.lib.web3.eth.abi.type.EthAddress
import io.hamal.lib.web3.eth.abi.type.EthHash
import io.hamal.lib.web3.eth.abi.type.EthUint64
import kotlinx.serialization.Serializable


@Serializable
data class EthBlock(
    val number: EthUint64,
    val hash: EthHash,
    val parentHash: EthHash,
    val miner: EthAddress,
    val timestamp: EthUint64
)

@Serializable
data class EthLiteBlock(
    val number: EthUint64,
    val hash: EthHash,
    val parentHash: EthHash,
    val miner: EthAddress,
    val timestamp: EthUint64
)