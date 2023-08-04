package io.hamal.lib.web3.hml

import io.hamal.lib.web3.eth.abi.EthFunction
import io.hamal.lib.web3.eth.abi.type.EthAddress
import io.hamal.lib.web3.eth.abi.type.EthPrefixedHexString
import io.hamal.lib.web3.eth.abi.type.EthUint64
import io.hamal.lib.web3.eth.domain.EthRequestId
import io.hamal.lib.web3.hml.domain.Chain
import io.hamal.lib.web3.hml.domain.HmlResponse


interface HmlBatchService<SERVICE : HmlBatchService<SERVICE>> {
    val chain: Chain

    fun execute(): List<HmlResponse>
    fun getBlock(number: EthUint64): SERVICE
    fun call(callRequest: CallRequest): SERVICE

    fun lastRequestId(): EthRequestId

    fun call(
        to: EthAddress,
        function: EthFunction<*, *>,
        blockNumber: EthUint64
    ): SERVICE {
        return call(
            CallRequest(
                to = to,
                data = function.signature.encoded.toPrefixedHexString(),
                blockNumber = blockNumber
            )
        )
    }

    data class CallRequest(
        val to: EthAddress,
        val data: EthPrefixedHexString,
        val blockNumber: EthUint64
    )
}