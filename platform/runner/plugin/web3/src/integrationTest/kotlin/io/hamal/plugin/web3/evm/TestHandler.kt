package io.hamal.plugin.web3.evm

import io.hamal.lib.common.serialization.json.JsonArray
import io.hamal.lib.common.serialization.json.JsonObject
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
import io.hamal.lib.web3.serde

object TestHandler {

    fun handle(chain: String, requests: JsonArray): JsonArray {
        val reqs = requests
            .filterIsInstance<JsonObject>()
            .map { request ->

                val (err, req) = if (chain == "eth") {
                    parseEthRequest(serde, request)
                } else {
                    parseArbitrumRequest(serde, request)
                }

                err ?: req
            }

        return reqs.filterIsInstance<EvmRequest>()
            .map { req -> handle(req) }
            .plus(reqs.filterIsInstance<EvmResponse>())
            .let { responses ->
                serde.read(JsonArray::class, serde.write(responses))
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
            ?.let { serde.read(ArbitrumBlockData::class, it) }

    private fun getEthBlock(id: EvmUint64): EthBlockData? =
        this.javaClass.getResourceAsStream("/fixture/eth/block_${id.value}_full.json")
            ?.let { serde.read(EthBlockData::class, it) }
}