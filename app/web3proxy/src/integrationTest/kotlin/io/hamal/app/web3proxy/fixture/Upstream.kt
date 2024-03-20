package io.hamal.app.web3proxy.fixture

import io.hamal.lib.web3.evm.abi.type.EvmUint64
import io.hamal.lib.web3.evm.domain.EvmRequestId
import io.hamal.lib.web3.evm.impl.eth.DepEthBatchService
import io.hamal.lib.web3.evm.impl.eth.domain.EthBlock
import io.hamal.lib.web3.evm.impl.eth.domain.EthGetBlockResponse
import io.hamal.lib.web3.evm.impl.eth.domain.EthResponse
import io.hamal.lib.web3.json

internal class EthBatchServiceFixture : DepEthBatchService<EthBatchServiceFixture> {

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
                    result = json.deserialize(EthBlock::class, String(blockData.readAllBytes()))
                )
            )
        }

        return this
    }

    override fun getBlockNumber(): EthBatchServiceFixture {
        TODO("Not yet implemented")
    }

    override fun getLiteBlock(number: EvmUint64): EthBatchServiceFixture {
        TODO("Not yet implemented")
    }

    override fun call(callRequest: DepEthBatchService.EthCallRequest): EthBatchServiceFixture {
        TODO("Not yet implemented")
    }

    override fun lastRequestId(): EvmRequestId {
        TODO("Not yet implemented")
    }

    fun clear() {
        responses.clear()
    }

    val responses = mutableListOf<EthResponse>()

}