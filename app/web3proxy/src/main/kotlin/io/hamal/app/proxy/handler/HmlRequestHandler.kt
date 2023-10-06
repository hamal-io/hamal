package io.hamal.app.proxy.handler

import io.hamal.app.proxy.cache.HmlCache
import io.hamal.lib.web3.eth.abi.type.EthUint64
import io.hamal.lib.web3.hml.HmlMethod
import io.hamal.lib.web3.hml.domain.HmlGetBlockResponse
import io.hamal.lib.web3.hml.domain.HmlRequestId
import io.hamal.lib.web3.hml.domain.HmlResponse
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray

interface HmlRequestHandler {
    fun handle(requests: List<Request>): List<HmlResponse>

    @Serializable
    data class Request(
        val id: HmlRequestId,
        val method: HmlMethod,
        val params: JsonArray,
    )
}

class DefaultHmlRequestHandler(
    private val json: Json,
    private val hmlCache: HmlCache
) : HmlRequestHandler {

    override fun handle(requests: List<HmlRequestHandler.Request>): List<HmlResponse> {
        // FIXME fetch data if not available yet
        return requests.mapNotNull { request ->
            when (request.method) {
                HmlMethod.GetBlockByNumber -> {
                    val blockId = json.decodeFromJsonElement(EthUint64.serializer(), request.params[0])
                    hmlCache.findBlock(blockId)?.let { block ->
                        HmlGetBlockResponse(request.id, block)
                    }
                }
                //FIXME request block if not there

                HmlMethod.Call -> TODO()
                else -> TODO()
            }
        }
    }
}