package io.hamal.lib.web3.evm.impl.eth.domain

import io.hamal.lib.web3.evm.abi.type.EvmAddress
import io.hamal.lib.web3.evm.abi.type.EvmHash
import io.hamal.lib.web3.evm.abi.type.EvmPrefixedHexString

data class Receipt(
    val transactionHash: EvmHash,
    val logs: List<Log>
) {
    data class Log(
        val address: EvmAddress,
        val data: EvmPrefixedHexString,
        val topics: List<EvmHash>
    )
}