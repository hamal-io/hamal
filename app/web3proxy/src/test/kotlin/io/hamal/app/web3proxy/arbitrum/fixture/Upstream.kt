package io.hamal.app.web3proxy.arbitrum.fixture

import io.hamal.lib.web3.evm.abi.type.EvmAddress
import io.hamal.lib.web3.evm.abi.type.EvmPrefixedHexString
import io.hamal.lib.web3.evm.abi.type.EvmUint64
import io.hamal.lib.web3.evm.domain.EvmRequestId
import io.hamal.lib.web3.evm.impl.arbitrum.domain.ArbitrumBlock
import io.hamal.lib.web3.evm.impl.arbitrum.domain.ArbitrumGetBlockResponse
import io.hamal.lib.web3.evm.impl.arbitrum.domain.ArbitrumResponse
import io.hamal.lib.web3.evm.impl.arbitrum.http.ArbitrumBatchService
import io.hamal.lib.web3.json

internal class ArbitrumBatchServiceFixture : ArbitrumBatchService<ArbitrumBatchServiceFixture> {

    override fun execute(): List<ArbitrumResponse> {
        return responses
    }

    override fun getBlock(number: EvmUint64): ArbitrumBatchServiceFixture {
        val blockData = this.javaClass.getResourceAsStream("/arbitrum/blocks/${number.value}.json")
        if (blockData == null) {
            responses.add(
                ArbitrumGetBlockResponse(
                    id = EvmRequestId(responses.size.toString()),
                    result = null
                )
            )
        } else {
            responses.add(
                ArbitrumGetBlockResponse(
                    id = EvmRequestId(responses.size.toString()),
                    result = json.deserialize(ArbitrumBlock::class, String(blockData.readAllBytes()))
                )
            )
        }

        return this
    }

    override fun call(to: EvmAddress, data: EvmPrefixedHexString, number: EvmUint64, from: EvmAddress?): ArbitrumBatchServiceFixture {
        TODO("Not yet implemented")
    }

    fun clear() {
        responses.clear()
    }

    val responses = mutableListOf<ArbitrumResponse>()
}