package io.hamal.app.proxy.handler

import io.hamal.app.proxy.cache.Cache
import io.hamal.lib.web3.eth.abi.type.EthBool
import io.hamal.lib.web3.eth.abi.type.EthUint64
import io.hamal.lib.web3.eth.domain.EthGetBlockResp
import io.hamal.lib.web3.eth.domain.EthMethod
import io.hamal.lib.web3.eth.domain.EthReqId
import io.hamal.lib.web3.eth.domain.EthResp
import io.hamal.lib.web3.eth.http.EthHttpBatchService
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean

interface EthRequestHandler {
    fun handle(requests: List<Request>): List<EthResp>

    @Serializable
    data class Request(
        val id: EthReqId,
        val method: EthMethod,
        val params: JsonArray,
    )
}

class DefaultEthRequestHandler(
    private val json: Json,
    private val cache: Cache
) : EthRequestHandler {

    override fun handle(requests: List<EthRequestHandler.Request>): List<EthResp> {
        // fixme assert each req id is unique

        // find requests which can not be served by cache / repository
        // create batch request
        // populate cache
        // result

        val resultMapping = mutableMapOf<EthReqId, EthResp>()

        requests.forEach { request ->
            when (request.method) {
                EthMethod.GetBlockByNumber -> {
                    val blockNumber = json.decodeFromJsonElement(EthUint64.serializer(), request.params[0])
                    cache.findBlock(blockNumber)?.let { block ->
                        resultMapping[request.id] = EthGetBlockResp(request.id, block)
                    }
                }

                else -> TODO()
            }
        }

        val batchService = EthHttpBatchService()

        requests.forEach { request ->
            when (request.method) {
                EthMethod.GetBlockByHash -> TODO()
                EthMethod.GetBlockByNumber -> {

                    if (!resultMapping.containsKey(request.id)) {
                        val blockNumber = json.decodeFromJsonElement(EthUint64.serializer(), request.params[0])
                        val fullBlock = EthBool((request.params[1] as JsonPrimitive).boolean)
                        batchService.getBlock(blockNumber)
                    }
                }
            }
        }

        // fixme respect incoming ids
        // assemble result

        val result = batchService.execute()

        result.forEach { ethResp ->
            when (ethResp) {
                is EthGetBlockResp -> cache.store(ethResp.result)
                else -> TODO()
            }
        }

        // FIXME requestId mapping is wrong and must be set properly
        return resultMapping.values.plus(result)
    }
}