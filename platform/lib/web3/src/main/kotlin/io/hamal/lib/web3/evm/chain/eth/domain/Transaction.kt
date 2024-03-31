package io.hamal.lib.web3.evm.chain.eth.domain

import io.hamal.lib.web3.evm.abi.type.*
import io.hamal.lib.web3.evm.domain.EvmTransactionData

data class EthTransactionData(
    val blockHash: EvmHash?,
    val blockNumber: EvmUint64?,
    val from: EvmAddress,
    val gas: EvmUint64,
    val gasPrice: EvmUint64,
    val maxPriorityFeePerGas: EvmUint64?,
    val maxFeePerGas: EvmUint64?,
    override val hash: EvmHash,
    val input: EvmPrefixedHexString?,
    val nonce: EvmUint64,
    val to: EvmAddress?,
    val transactionIndex: EvmUint32?,
    val value: EvmUint256?,
    val type: EvmUint8,
    val accessList: List<AccessListItem>?
) : EvmTransactionData {
    data class AccessListItem(
        val address: EvmAddress,
        val storageKeys: List<EvmHash>
    )
}

