package io.hamal.app.web3proxy.arbitrum.fixture

import io.hamal.lib.web3.evm.abi.type.EvmAddress
import io.hamal.lib.web3.evm.abi.type.EvmPrefixedHexString
import io.hamal.lib.web3.evm.abi.type.EvmUint64
import io.hamal.lib.web3.evm.chain.arbitrum.domain.ArbitrumBlockData
import io.hamal.lib.web3.evm.chain.arbitrum.domain.ArbitrumGetBlockResponse
import io.hamal.lib.web3.evm.chain.arbitrum.domain.ArbitrumResponse
import io.hamal.lib.web3.evm.chain.arbitrum.http.ArbitrumBatchService
import io.hamal.lib.web3.evm.domain.EvmRequestId
import io.hamal.lib.web3.serde

internal class ArbitrumBatchServiceFixture : ArbitrumBatchService<ArbitrumBatchServiceFixture> {

    override fun execute(): List<ArbitrumResponse> {
        return responses
    }

    override fun sendRawTransaction(data: EvmPrefixedHexString): ArbitrumBatchServiceFixture {
        TODO("Not yet implemented")
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
                    result = serde.read(ArbitrumBlockData::class, String(blockData.readAllBytes()))
                )
            )
        }

        return this
    }

    override fun call(to: EvmAddress, data: EvmPrefixedHexString, number: EvmUint64, from: EvmAddress?): ArbitrumBatchServiceFixture {
        TODO("Not yet implemented")
    }

    private val responses = mutableListOf<ArbitrumResponse>()

}