package io.hamal.extension.web3.eth

import io.hamal.lib.common.serialization.serde.SerdeArray
import io.hamal.lib.common.serialization.serde.SerdeObject
import io.hamal.lib.web3.evm.abi.type.EvmUint64
import io.hamal.lib.web3.evm.chain.eth.domain.EthBlockData
import io.hamal.lib.web3.evm.chain.eth.domain.EthGetBlockByNumberRequest
import io.hamal.lib.web3.evm.chain.eth.domain.EthGetBlockResponse
import io.hamal.lib.web3.evm.chain.eth.domain.parseEthRequest
import io.hamal.lib.web3.evm.domain.EvmRequest
import io.hamal.lib.web3.evm.domain.EvmResponse
import io.hamal.lib.web3.json

object TestHandler {

    fun handle(requests: SerdeArray): SerdeArray {
        val reqs = requests
            .filterIsInstance<SerdeObject>()
            .map { request ->
                val (err, req) = parseEthRequest(json, request)
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
            is EthGetBlockByNumberRequest -> {
                EthGetBlockResponse(
                    id = request.id,
                    result = getBlock(request.number)
                )
            }

            else -> TODO()
        }
    }

    private fun getBlock(id: EvmUint64): EthBlockData? =
        this.javaClass.getResourceAsStream("/fixture/block_${id.value}_full.json")
            ?.let { json.deserialize(EthBlockData::class, it) }
}