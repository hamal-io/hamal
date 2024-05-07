//package io.hamal.extension.web3.arbitrum
//
//import io.hamal.lib.common.serialization.json.JsonArray
//import io.hamal.lib.common.serialization.json.JsonObject
//import io.hamal.lib.web3.evm.abi.type.EvmUint64
//import io.hamal.lib.web3.evm.chain.arbitrum.domain.ArbitrumBlockData
//import io.hamal.lib.web3.evm.chain.arbitrum.domain.ArbitrumGetBlockByNumberRequest
//import io.hamal.lib.web3.evm.chain.arbitrum.domain.ArbitrumGetBlockResponse
//import io.hamal.lib.web3.evm.chain.arbitrum.domain.parseArbitrumRequest
//import io.hamal.lib.web3.evm.domain.EvmRequest
//import io.hamal.lib.web3.evm.domain.EvmResponse
//import io.hamal.lib.web3.serde
//
//object TestHandler {
//
//    fun handle(requests: JsonArray): JsonArray {
//        val reqs = requests
//            .filterIsInstance<JsonObject>()
//            .map { request ->
//                val (err, req) = parseArbitrumRequest(serde, request)
//                err ?: req
//            }
//
//        return reqs.filterIsInstance<EvmRequest>()
//            .map { req -> handle(req) }
//            .plus(reqs.filterIsInstance<EvmResponse>())
//            .let { responses ->
//                serde.read(JsonArray::class, serde.write(responses))
//            }
//    }
//
//
//    private fun handle(request: EvmRequest): EvmResponse {
//        return when (request) {
//            is ArbitrumGetBlockByNumberRequest -> {
//                ArbitrumGetBlockResponse(
//                    id = request.id,
//                    result = getBlock(request.number)
//                )
//            }
//
//            else -> TODO()
//        }
//    }
//
//    private fun getBlock(id: EvmUint64): ArbitrumBlockData? =
//        this.javaClass.getResourceAsStream("/fixture/block_${id.value}_full.json")
//            ?.let { serde.read(ArbitrumBlockData::class, it) }
//}