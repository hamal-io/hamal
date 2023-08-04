package io.hamal.app.proxy.handler

import io.hamal.app.proxy.cache.Cache
import io.hamal.app.proxy.domain.EthCall
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.web3.eth.EthBatchService
import io.hamal.lib.web3.eth.abi.type.EthAddress
import io.hamal.lib.web3.eth.abi.type.EthBool
import io.hamal.lib.web3.eth.abi.type.EthPrefixedHexString
import io.hamal.lib.web3.eth.abi.type.EthUint64
import io.hamal.lib.web3.eth.domain.*
import io.hamal.lib.web3.eth.http.EthHttpBatchService
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

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
    private val cache: Cache,
    private val httpTemplate: HttpTemplate
) : EthRequestHandler {

    override fun handle(requests: List<EthRequestHandler.Request>): List<EthResp> {
        // fixme assert each req id is unique

        // find requests which can not be served by cache / repository
        // create batch request
        // populate cache
        // result

        val resultMapping = mutableMapOf<EthReqId, EthResp>()
        val callReqMapping = mutableMapOf<EthReqId, EthBatchService.EthCallRequest>()

        requests.forEach { request ->
            when (request.method) {
                EthMethod.GetBlockByNumber -> {
                    val blockId = json.decodeFromJsonElement(EthUint64.serializer(), request.params[0])
                    cache.findBlock(blockId)?.let { block ->
                        resultMapping[request.id] = EthGetBlockResp(request.id, block)
                    }
                }

                EthMethod.Call -> {
                    val params = json.decodeFromJsonElement(JsonObject.serializer(), request.params[0])
                    val to = EthAddress(EthPrefixedHexString(params.get("to")!!.jsonPrimitive.content))
                    val data = EthPrefixedHexString(params.get("data")!!.jsonPrimitive.content)
                    val blockId = json.decodeFromJsonElement(EthUint64.serializer(), request.params[1])
                    cache.findCall(blockId, to, data)?.let { call ->
                        resultMapping[request.id] = EthCallResp(request.id, call.result)
                    }
                }

                else -> TODO()
            }
        }

        val batchService = EthHttpBatchService(httpTemplate)

        requests.forEach { request ->
            when (request.method) {
                EthMethod.GetBlockByNumber -> {
                    if (!resultMapping.containsKey(request.id)) {
                        val blockId = json.decodeFromJsonElement(EthUint64.serializer(), request.params[0])
                        val fullBlock = EthBool((request.params[1] as JsonPrimitive).boolean)
                        batchService.getBlock(blockId)
                    }
                }

                EthMethod.Call -> {
                    if (!resultMapping.containsKey(request.id)) {
                        val params = json.decodeFromJsonElement(JsonObject.serializer(), request.params[0])
                        val to = EthAddress(EthPrefixedHexString(params.get("to")!!.jsonPrimitive.content))
                        val data = EthPrefixedHexString(params.get("data")!!.jsonPrimitive.content)
                        val blockId = json.decodeFromJsonElement(EthUint64.serializer(), request.params[1])

                        val callReq = EthBatchService.EthCallRequest(
                            to = to,
                            data = data,
                            blockNumber = blockId,
                        )

                        batchService.call(callReq)

                        callReqMapping[batchService.lastRequestId()] = callReq
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
                is EthCallResp -> {
                    val call = callReqMapping[ethResp.id]!!

                    cache.store(
                        EthCall(
                            blockId = call.blockNumber,
                            to = call.to,
                            data = call.data,
                            result = ethResp.result
                        )
                    )
                }

                else -> TODO()
            }
        }

        // FIXME requestId mapping is wrong and must be set properly
        return resultMapping.values.plus(result)
    }
}