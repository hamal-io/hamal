package io.hamal.app.proxy.handler

import io.hamal.app.proxy.cache.EthCache
import io.hamal.app.proxy.domain.EthCall
import io.hamal.app.proxy.json
import io.hamal.lib.common.hot.HotArray
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.web3.eth.EthBatchService
import io.hamal.lib.web3.eth.abi.type.EthUint64
import io.hamal.lib.web3.eth.domain.*
import io.hamal.lib.web3.eth.http.EthHttpBatchService

interface EthRequestHandler {
    fun handle(requests: List<Request>): List<EthResponse>

    data class Request(
        val id: EthRequestId,
        val method: EthMethod,
        val params: HotArray,
    )
}

class EthRequestHandlerImpl(
    private val ethCache: EthCache,
    private val httpTemplate: HttpTemplateImpl
) : EthRequestHandler {

    override fun handle(requests: List<EthRequestHandler.Request>): List<EthResponse> {
        // fixme assert each request id is unique

        // find requests which can not be served by cache / repository
        // append batch request
        // populate cache
        // result

        val resultMapping = mutableMapOf<EthRequestId, EthResponse>()
        val callReqMapping = mutableMapOf<EthRequestId, EthBatchService.EthCallRequest>()

        requests.forEach { request ->
            when (request.method) {
                EthMethod.GetBlockByNumber -> {
                    val blockId = json.deserialize(EthUint64::class, request.params[0].stringValue)
                    ethCache.findBlock(blockId)?.let { block ->
//                        val dummyBlock = DummyBlock(
//                            number = block.number,
//                            gasLimit = block.gasLimit,
//                            gasUsed = block.gasUsed,
//                            timestamp = block.timestamp
//                        )
                        resultMapping[request.id] = EthGetBlockResponse(request.id, block)
//                        resultMapping[request.id] = EthGetBlockResponse(request.id, dummyBlock)
                    }
                }

//                EthMethod.Call -> {
//                    val params = json.deserialize(JsonObject.serializer(), request.params[0])
//                    val to = EthAddress(EthPrefixedHexString(params.get("to")!!.jsonPrimitive.content))
//                    val data = EthPrefixedHexString(params.get("data")!!.jsonPrimitive.content)
//                    val blockId = json.deserialize(EthUint64.serializer(), request.params[1])
//                    ethCache.findCall(blockId, to, data)?.let { call ->
//                        resultMapping[request.id] = EthCallResponse(request.id, call.result)
//                    }
//                }

                else -> TODO()
            }
        }

        val batchService = EthHttpBatchService(httpTemplate)

        requests.forEach { request ->
            when (request.method) {
                EthMethod.GetBlockByNumber -> {
                    if (!resultMapping.containsKey(request.id)) {
                        val blockId = json.deserialize(EthUint64::class, request.params[0].stringValue)
//                        val fullBlock = EthBool((request.params[1] as HotBoolean).booleanValue)
                        batchService.getBlock(blockId)
                    }
                }

                EthMethod.Call -> {
                    TODO()
                }
//                    if (!resultMapping.containsKey(request.id)) {
//                        val params = json.deserialize(HotObject::class, request.params[0].stringValue)
//                        val to = EthAddress(EthPrefixedHexString(params.get("to").jsonPrimitive.content))
//                        val data = EthPrefixedHexString(params.get("data").jsonPrimitive.content)
////                        val blockId = json.decodeFromJsonElement(EthUint64.serializer(), request.params[1])
//
//                        val callReq = EthBatchService.EthCallRequest(
//                            to = to,
//                            data = data,
//                            blockNumber = blockId,
//                        )
//
//                        batchService.call(callReq)
//
//                        callReqMapping[batchService.lastRequestId()] = callReq
//                    }
//                }
            }
        }

        // fixme respect incoming ids
        // assemble result

        val result = batchService.execute()

        result.forEach { ethResp ->
            when (ethResp) {
                is EthGetBlockResponse -> ethCache.store(ethResp.result)
                is EthCallResponse -> {
                    val call = callReqMapping[ethResp.id]!!

                    ethCache.store(
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

//         FIXME requestId mapping is wrong and must be set properly
        return resultMapping.values.plus(result)
    }
}