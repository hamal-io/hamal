package io.hamal.lib.web3.evm.impl.eth

import io.hamal.lib.web3.evm.abi.EthFunction
import io.hamal.lib.web3.evm.abi.type.EvmAddress
import io.hamal.lib.web3.evm.abi.type.EvmPrefixedHexString
import io.hamal.lib.web3.evm.abi.type.EvmUint64
import io.hamal.lib.web3.evm.domain.EvmRequestId
import io.hamal.lib.web3.evm.impl.eth.domain.EthResponse


interface EthBatchService<SERVICE : EthBatchService<SERVICE>> {
    fun execute(): List<EthResponse>
    fun getBlockNumber(): SERVICE
    fun getBlock(number: EvmUint64): SERVICE
    fun getLiteBlock(number: EvmUint64): SERVICE
    fun call(callRequest: EthCallRequest): SERVICE

    fun lastRequestId(): EvmRequestId

    fun call(
        to: EvmAddress,
        function: EthFunction<*, *>,
        blockNumber: EvmUint64
    ): SERVICE {
        return call(
            EthCallRequest(
                to = to,
                data = function.signature.encoded.toPrefixedHexString(),
                blockNumber = blockNumber
            )
        )
    }

    data class EthCallRequest(
        val to: EvmAddress,
        val data: EvmPrefixedHexString,
        val blockNumber: EvmUint64
    )
}