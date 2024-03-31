package io.hamal.lib.web3.evm.domain

import io.hamal.lib.web3.evm.abi.type.EvmAddress
import io.hamal.lib.web3.evm.abi.type.EvmHash

data class EvmAccessList(
    val address: EvmAddress,
    val storageKeys: List<EvmHash>
)