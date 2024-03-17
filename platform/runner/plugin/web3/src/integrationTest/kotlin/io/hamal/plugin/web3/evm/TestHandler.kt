package io.hamal.plugin.web3.evm

import io.hamal.lib.common.hot.HotArray
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.web3.eth.abi.type.EthUint64
import io.hamal.lib.web3.eth.domain.*
import io.hamal.lib.web3.eth.json

object TestHandler {

    fun handle(chain: String, requests: HotArray): HotArray {
        val reqs = requests
            .filterIsInstance<HotObject>()
            .map { request ->
                val (err, req) = parseRequest(json, request)
                if (err != null) {
                    err
                } else {
                    req
                }
            }

        return reqs.filterIsInstance<EthRequest>()
            .map { req -> handle(chain, req) }
            .plus(reqs.filterIsInstance<EthResponse>())
            .let { responses ->
                json.deserialize(HotArray::class, json.serialize(responses))
            }
    }


    private fun handle(chain: String, request: EthRequest): EthResponse {
        return when (request) {
            is EthGetBlockByNumberRequest -> {
                EthGetBlockResponse(
                    id = request.id,
                    result = loadBlock(chain, request.number)
                )
            }

            else -> TODO()
        }
    }

    private fun loadBlock(chain: String, id: EthUint64): EthBlock? = this.javaClass.getResourceAsStream("/fixture/${chain}/block_${id.value}_full.json")
        ?.let { json.deserialize(EthBlock::class, it) }
}