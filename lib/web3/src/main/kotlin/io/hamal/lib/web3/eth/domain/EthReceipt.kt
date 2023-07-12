package io.hamal.lib.web3.eth.domain

import io.hamal.lib.web3.eth.abi.type.EthAddress
import io.hamal.lib.web3.eth.abi.type.EthHash
import io.hamal.lib.web3.eth.abi.type.EthPrefixedHexString
import kotlinx.serialization.Serializable

@Serializable
data class EthReceipt(
    val transactionHash: EthHash,
    val logs: List<Log>
) {
    @Serializable
    data class Log(
        val address: EthAddress,
        val data: EthPrefixedHexString,
        val topics: List<EthHash>
    )
}