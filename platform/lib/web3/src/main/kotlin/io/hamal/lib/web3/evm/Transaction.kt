package io.hamal.lib.web3.evm

import io.hamal.lib.web3.evm.abi.type.EvmAddress
import io.hamal.lib.web3.evm.abi.type.EvmPrefixedHexString
import io.hamal.lib.web3.evm.abi.type.EvmUint256
import io.hamal.lib.web3.evm.abi.type.EvmUint64
import io.hamal.lib.web3.evm.domain.EvmAccessList

sealed interface EvmTransaction {

    val chainId: ChainId
    val nonce: EvmUint64
    val gasLimit: EvmUint64
    val to: EvmAddress

    data class Eip1559(
        override val chainId: ChainId,
        override val nonce: EvmUint64,
        override val gasLimit: EvmUint64,
        override val to: EvmAddress,
        val value: EvmUint256,
        val data: EvmPrefixedHexString,
        val maxPriorityFeePerGas: EvmUint64,
        val maxFeePerGas: EvmUint64,
        val accessList: EvmAccessList
    ) : EvmTransaction

}