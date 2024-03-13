package io.hamal.app.web3proxy.fixture

import io.hamal.app.web3proxy.json
import io.hamal.lib.web3.eth.EthBatchService
import io.hamal.lib.web3.eth.abi.type.EthUint64
import io.hamal.lib.web3.eth.domain.EthBlock
import io.hamal.lib.web3.eth.domain.EthGetBlockResponse
import io.hamal.lib.web3.eth.domain.EthRequestId
import io.hamal.lib.web3.eth.domain.EthResponse

internal class EthBatchServiceFixture : EthBatchService<EthBatchServiceFixture> {

    override fun execute(): List<EthResponse> {
        return responses
    }

    override fun getBlock(number: EthUint64): EthBatchServiceFixture {
        val blockData = this.javaClass.getResourceAsStream("/eth/blocks/${number.value}.json")
        if (blockData == null) {
            responses.add(
                EthGetBlockResponse(
                    id = EthRequestId(responses.size.toString()),
                    result = null
                )
            )
        } else {
            responses.add(
                EthGetBlockResponse(
                    id = EthRequestId(responses.size.toString()),
                    result = json.deserialize(EthBlock::class, String(blockData.readAllBytes()))
                )
            )
        }

        return this
    }

    override fun getBlockNumber(): EthBatchServiceFixture {
        TODO("Not yet implemented")
    }

    override fun getLiteBlock(number: EthUint64): EthBatchServiceFixture {
        TODO("Not yet implemented")
    }

    override fun call(callRequest: EthBatchService.EthCallRequest): EthBatchServiceFixture {
        TODO("Not yet implemented")
    }

    override fun lastRequestId(): EthRequestId {
        TODO("Not yet implemented")
    }

    fun clear() {
        responses.clear()
    }

    val responses = mutableListOf<EthResponse>()
}