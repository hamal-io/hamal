package io.hamal.plugin.web3.evm

import io.hamal.lib.common.serialization.serde.SerdeArray
import io.hamal.lib.common.serialization.serde.SerdeObject
import io.hamal.lib.web3.evm.abi.type.EvmUint64
import io.hamal.lib.web3.evm.domain.EvmRequest
import io.hamal.lib.web3.evm.domain.EvmResponse
import io.hamal.lib.web3.evm.chain.arbitrum.domain.ArbitrumBlockData
import io.hamal.lib.web3.evm.chain.arbitrum.domain.ArbitrumGetBlockByNumberRequest
import io.hamal.lib.web3.evm.chain.arbitrum.domain.ArbitrumGetBlockResponse
import io.hamal.lib.web3.evm.chain.arbitrum.domain.parseArbitrumRequest
import io.hamal.lib.web3.evm.chain.eth.domain.EthBlockData
import io.hamal.lib.web3.evm.chain.eth.domain.EthGetBlockByNumberRequest
import io.hamal.lib.web3.evm.chain.eth.domain.EthGetBlockResponse
import io.hamal.lib.web3.evm.chain.eth.domain.parseEthRequest
import io.hamal.lib.web3.json

object TestHandler {

    fun handle(chain: String, requests: SerdeArray): SerdeArray {
        val reqs = requests
            .filterIsInstance<SerdeObject>()
            .map { request ->

                val (err, req) = if (chain == "eth") {
                    parseEthRequest(json, request)
                } else {
                    parseArbitrumRequest(json, request)
                }

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
                    result = getArbitrumBlock(request.number)
                )
            }

            is EthGetBlockByNumberRequest -> {
                EthGetBlockResponse(
                    id = request.id,
                    result = getEthBlock(request.number)
                )
            }

            else -> TODO()
        }
    }

    private fun getArbitrumBlock(id: EvmUint64): ArbitrumBlockData? =
        this.javaClass.getResourceAsStream("/fixture/arbitrum/block_${id.value}_full.json")
            ?.let { json.deserialize(ArbitrumBlockData::class, it) }

    private fun getEthBlock(id: EvmUint64): EthBlockData? =
        this.javaClass.getResourceAsStream("/fixture/eth/block_${id.value}_full.json")
            ?.let { json.deserialize(EthBlockData::class, it) }
}