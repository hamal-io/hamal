package io.hamal.lib.web3.evm.domain

import io.hamal.lib.web3.evm.abi.type.EvmHash

interface EvmTransaction {
    val hash: EvmHash
}