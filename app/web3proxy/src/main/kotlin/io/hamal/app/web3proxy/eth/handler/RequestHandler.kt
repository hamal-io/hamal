package io.hamal.app.web3proxy.eth.handler

import io.hamal.app.web3proxy.eth.repository.EthBlockDataRepository
import io.hamal.lib.web3.eth.domain.*

interface HandleEthRequest {
    operator fun invoke(request: EthRequest): EthResponse
    operator fun invoke(requests: List<EthRequest>): List<EthResponse>

}

class EthRequestHandlerImpl(
    private val blockDataRepository: EthBlockDataRepository,
) : HandleEthRequest {

    override fun invoke(request: EthRequest): EthResponse {
        return invoke(listOf(request)).firstOrNull()
            ?: throw RuntimeException("Unable to process request")
    }

    override fun invoke(requests: List<EthRequest>): List<EthResponse> {
//        val template = HttpTemplateImpl("")


        val blocks = requests.filterIsInstance<EthGetBlockByNumberRequest>().map { it.number }

        return blockDataRepository.list(blocks).map { block ->
            EthGetBlockResponse(
                id = EthRequestId(1),
                result = block
            )
        }

//        val requestedBlocksByNumbers = requests.filterIsInstance<HotObject>()
//            .filter { it.stringValue("method") == "eth_getBlockByNumber" }
//            .map { it.asArray("params") }
//            .map { params ->
//                val fullBlock = if (params.isBoolean(1)) {
//                    params.booleanValue(1)
//                } else {
//                    true
//                }
//                EthUint64(EthPrefixedHexString(params[0].stringValue)) to fullBlock
//            }

//        val blocks = blockDataRepository.list(requestedBlocksByNumbers.map { it.first })

        // map full or partial representation


//        blockDataRepository.


//        val response = template
//            .post("https://rpc.ankr.com/eth/dc1bb7e07d13bf1a2fe5e8f1662664c39abc5413f6f96e1fb6145ed3abe06842")
//            .body(
//                HotArray.builder()
//                    .append(
//                        HotObject.builder()
//                            .set("id", "1")
//                            .set("jsonrpc", "2.0")
//                            .set("method", "eth_getBlockByNumber")
//                            .set("params", HotArray.builder().append("0x1").append(true).build())
//                            .build()
//                    ).build()
//            )
//            .execute(HotArray::class)


//        return listOf()
    }
}