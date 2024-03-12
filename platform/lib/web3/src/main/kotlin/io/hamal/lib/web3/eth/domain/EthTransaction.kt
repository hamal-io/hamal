package io.hamal.lib.web3.eth.domain

import io.hamal.lib.web3.eth.abi.type.*

data class EthTransaction(
    val blockHash: EthHash?,
    val blockNumber: EthUint64?,
    val from: EthAddress?,
    val gas: EthUint64?,
    val gasPrice: EthUint64?,
    val maxPriorityFeePerGas: EthUint64?,
    val maxFeePerGas: EthUint64?,
    val hash: EthHash?,
    val input: EthPrefixedHexString?,
    val nonce: EthUint64?,
    val to: EthAddress?,
    val transactionIndex: EthUint32?,
    val value: EthUint256?,
    val type: EthUint8?,
    val accessList: List<AccessListItem>
) {
    data class AccessListItem(
        val address: EthAddress,
        val storageKeys: List<EthHash>
    )
}

