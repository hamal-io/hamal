package io.hamal.lib.web3.evm.impl.arbitrum.domain

import io.hamal.lib.web3.evm.abi.type.*
import io.hamal.lib.web3.evm.domain.EvmTransaction

data class ArbitrumTransaction(
    val blockHash: EvmHash?,
    val blockNumber: EvmUint64?,
    val from: EvmAddress,
    val gas: EvmUint64,
    val gasPrice: EvmUint64,
    override val hash: EvmHash,
    val input: EvmPrefixedHexString?,
    val nonce: EvmUint64,
    val to: EvmAddress?,
    val transactionIndex: EvmUint32?,
    val value: EvmUint256?,
    val type: EvmUint8,
    val requestId: EvmUint64?
) : EvmTransaction

