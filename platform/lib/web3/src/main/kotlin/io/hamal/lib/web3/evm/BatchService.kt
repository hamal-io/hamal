package io.hamal.lib.web3.evm

import io.hamal.lib.web3.evm.abi.type.EvmAddress
import io.hamal.lib.web3.evm.abi.type.EvmPrefixedHexString
import io.hamal.lib.web3.evm.abi.type.EvmUint64
import io.hamal.lib.web3.evm.domain.EvmResponse

interface EvmBatchService<out RESPONSE : EvmResponse, out SERVICE : EvmBatchService<RESPONSE, SERVICE>> {
    fun getBlock(number: EvmUint64): SERVICE
    fun call(to: EvmAddress, data: EvmPrefixedHexString, number: EvmUint64, from: EvmAddress? = null): SERVICE
    fun execute(): List<RESPONSE>
}

