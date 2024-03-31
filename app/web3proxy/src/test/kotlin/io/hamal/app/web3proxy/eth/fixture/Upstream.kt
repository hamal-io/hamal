package io.hamal.app.web3proxy.eth.fixture

import io.hamal.lib.web3.evm.abi.type.EvmAddress
import io.hamal.lib.web3.evm.abi.type.EvmPrefixedHexString
import io.hamal.lib.web3.evm.abi.type.EvmUint64
import io.hamal.lib.web3.evm.domain.EvmRequestId
import io.hamal.lib.web3.evm.chain.eth.domain.EthBlockData
import io.hamal.lib.web3.evm.chain.eth.domain.EthGetBlockResponse
import io.hamal.lib.web3.evm.chain.eth.domain.EthResponse
import io.hamal.lib.web3.evm.chain.eth.http.EthBatchService
import io.hamal.lib.web3.json

internal class EthBatchServiceFixture : EthBatchService<EthBatchServiceFixture> {

    override fun execute(): List<EthResponse> {
        return responses
    }

    override fun getBlock(number: EvmUint64): EthBatchServiceFixture {
        val blockData = this.javaClass.getResourceAsStream("/eth/blocks/${number.value}.json")
        if (blockData == null) {
            responses.add(
                EthGetBlockResponse(
                    id = EvmRequestId(responses.size.toString()),
                    result = null
                )
            )
        } else {
            responses.add(
                EthGetBlockResponse(
                    id = EvmRequestId(responses.size.toString()),
                    result = json.deserialize(EthBlockData::class, String(blockData.readAllBytes()))
                )
            )
        }

        return this
    }

    override fun call(to: EvmAddress, data: EvmPrefixedHexString, number: EvmUint64, from: EvmAddress?): EthBatchServiceFixture {
        TODO("Not yet implemented")
    }

    fun clear() {
        responses.clear()
    }

    val responses = mutableListOf<EthResponse>()
}