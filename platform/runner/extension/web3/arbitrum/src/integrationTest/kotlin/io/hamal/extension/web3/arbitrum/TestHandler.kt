package io.hamal.extension.web3.arbitrum

import io.hamal.lib.common.serialization.serde.SerdeArray
import io.hamal.lib.common.serialization.serde.SerdeObject
import io.hamal.lib.web3.evm.abi.type.EvmUint64
import io.hamal.lib.web3.evm.chain.arbitrum.domain.ArbitrumBlockData
import io.hamal.lib.web3.evm.chain.arbitrum.domain.ArbitrumGetBlockByNumberRequest
import io.hamal.lib.web3.evm.chain.arbitrum.domain.ArbitrumGetBlockResponse
import io.hamal.lib.web3.evm.chain.arbitrum.domain.parseArbitrumRequest
import io.hamal.lib.web3.evm.domain.EvmRequest
import io.hamal.lib.web3.evm.domain.EvmResponse
import io.hamal.lib.web3.json

object TestHandler {

    fun handle(requests: SerdeArray): SerdeArray {
        val reqs = requests
            .filterIsInstance<SerdeObject>()
            .map { request ->
                val (err, req) = parseArbitrumRequest(json, request)
                err ?: req
            }

        return reqs.filterIsInstance<EvmRequest>()
            .map { req -> handle(req) }
            .plus(reqs.filterIsInstance<EvmResponse>())
            .let { responses ->
                json.deserialize(SerdeArray::class, json.serialize(responses))
            }
    }


    private fun handle(request: EvmRequest): EvmResponse {
        return when (request) {
            is ArbitrumGetBlockByNumberRequest -> {
                ArbitrumGetBlockResponse(
                    id = request.id,
                    result = getBlock(request.number)
                )
            }

            else -> TODO()
        }
    }

    private fun getBlock(id: EvmUint64): ArbitrumBlockData? =
        this.javaClass.getResourceAsStream("/fixture/block_${id.value}_full.json")
            ?.let { json.deserialize(ArbitrumBlockData::class, it) }
}