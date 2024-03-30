package io.hamal.lib.web3.evm.domain

import io.hamal.lib.web3.evm.abi.type.EvmHash

interface EvmTransactionData {
    val hash: EvmHash
}