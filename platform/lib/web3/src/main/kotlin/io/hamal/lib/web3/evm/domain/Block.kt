package io.hamal.lib.web3.evm.domain

import io.hamal.lib.web3.evm.abi.type.EvmHash
import io.hamal.lib.web3.evm.abi.type.EvmUint64

interface EvmBlock {
    val number: EvmUint64
    val hash: EvmHash
}