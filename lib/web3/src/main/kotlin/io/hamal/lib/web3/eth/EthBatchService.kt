package io.hamal.lib.web3.eth

import io.hamal.lib.web3.eth.abi.EthFunction
import io.hamal.lib.web3.eth.abi.type.EthAddress
import io.hamal.lib.web3.eth.abi.type.EthPrefixedHexString
import io.hamal.lib.web3.eth.abi.type.EthUint64
import io.hamal.lib.web3.eth.domain.EthResp


interface EthBatchService<SERVICE : EthBatchService<SERVICE>> {
    fun execute(): List<EthResp>
    fun getBlockNumber(): SERVICE
    fun getBlock(number: EthUint64): SERVICE
    fun getLiteBlock(number: EthUint64): SERVICE
    fun call(callRequest: EthCallRequest): SERVICE

    fun callFunction(
        to: EthAddress,
        function: EthFunction<*, *>,
        blockNumber: EthUint64
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
        val to: EthAddress,
        val data: EthPrefixedHexString,
        val blockNumber: EthUint64
    )
}