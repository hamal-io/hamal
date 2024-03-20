package io.hamal.lib.web3.evm

import io.hamal.lib.common.hot.HotArray
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.hot.HotObjectBuilder
import io.hamal.lib.domain.Json
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.web3.evm.abi.type.EvmUint64
import io.hamal.lib.web3.evm.domain.EvmResponse
import kotlin.reflect.KClass

interface EvmBatchService<out RESPONSE : EvmResponse, out SERVICE : EvmBatchService<RESPONSE, SERVICE>> {
    fun getBlock(number: EvmUint64): SERVICE
    fun execute(): List<RESPONSE>
}

abstract class HttpBaseBatchService<out RESPONSE : EvmResponse>(
    private val httpTemplate: HttpTemplate,
    private val json: Json
) {

    fun execute(): List<RESPONSE> {
        if (requests.isEmpty()) {
            return listOf()
        }

        val response = httpTemplate
            .post()
            .body(HotArray.builder().also { builder -> requests.forEach { request -> builder.append(request) } }.build())
            .execute(HotArray::class)

        return response.nodes.mapIndexed { index, hotNode ->
            val result = hotNode.asObject()
            json.deserialize(resultClasses[index], json.serialize(result))
        }
            .also {
                requests.clear()
                resultClasses.clear()
            }
    }

    protected fun request(
        method: String,
        params: HotArray,
        resultClass: KClass<out @UnsafeVariance RESPONSE>
    ) {
        addRequest(
            createReq = { id ->
                HotObjectBuilder()
                    .set("jsonrpc", "2.0")
                    .set("id", id.toString())
                    .set("method", method)
                    .set("params", params)
                    .build(
                    )
            },
            resultClass = resultClass
        )
    }

    private fun addRequest(createReq: (Int) -> HotObject, resultClass: KClass<out RESPONSE>) {
        val reqId = requests.size + 1
        resultClasses.add(resultClass)
        requests.add(createReq(reqId))
    }

    private val resultClasses = mutableListOf<KClass<out RESPONSE>>()
    private val requests = mutableListOf<HotObject>()
}

//class DepEthHttpBatchService(
//    private val httpTemplate: HttpTemplate
//) : DepEthBatchService<DepEthHttpBatchService> {
//
//    private val resultClasses = mutableListOf<KClass<*>>()
//    private val requests = mutableListOf<HotObject>()
//
//    override fun getBlockNumber() = request(
//        method = "eth_blockNumber",
//        params = HotArray.empty,
//        resultClass = EthGetBlockNumberResponse::class
//    )
//
//    override fun getBlock(number: EvmUint64) = request(
//        method = "eth_getBlockByNumber",
//        params = HotArray.builder()
//            .append(number.toPrefixedHexString().value)
//            .append(true)
//            .build(),
//        resultClass = EthGetBlockResponse::class
//    )
//
//    override fun getLiteBlock(number: EvmUint64) = request(
//        method = "eth_getBlockByNumber",
//        params = HotArray.builder()
//            .append(number.toPrefixedHexString().value)
//            .append(false)
//            .build(),
//        resultClass = EthGetLiteBlockResponse::class
//    )
//
//    override fun call(callRequest: DepEthBatchService.EthCallRequest) = request(
//        method = "eth_call",
//        params = HotArray.builder()
//            .append(
//                HotObject.builder()
//                    .set("to", callRequest.to.toPrefixedHexString().value)
//                    .set("data", callRequest.data.value)
//                    .build()
//            )
//            .append(callRequest.blockNumber.toPrefixedHexString().value)
//            .build(),
//        resultClass = EthCallResponse::class
//    )
//
//    override fun lastRequestId(): EvmRequestId {
//        return EvmRequestId(requests.size.toString())
//    }
//
//    override fun execute(): List<EthResponse> {
//        if (requests.isEmpty()) {
//            return listOf()
//        }
//
//        val response = httpTemplate
//            .post()
//            .body(HotArray.builder().also { builder -> requests.forEach { request -> builder.append(request) } }.build())
//            .execute(HotArray::class)
//
//        return response.nodes.mapIndexed { index, hotNode ->
//            val response = hotNode.asObject()
//            json.deserialize(resultClasses[index], json.serialize(response))
//        }
//            .filterIsInstance<EthResponse>()
//            .also {
//                requests.clear()
//                resultClasses.clear()
//            }
//    }
//
//    internal val json = Json(
//        JsonFactoryBuilder()
//            .register(EvmHotModule)
//            .register(HotObjectModule)
//    )
//
//    private fun <RESPONSE : EthResponse> request(
//        method: String,
//        params: HotArray,
//        resultClass: KClass<RESPONSE>
//    ): DepEthHttpBatchService {
//        addRequest(
//            createReq = { id ->
//                HotObjectBuilder()
//                    .set("jsonrpc", "2.0")
//                    .set("id", id.toString())
//                    .set("method", method)
//                    .set("params", params)
//                    .build(
//                    )
//            },
//            resultClass = resultClass
//        )
//        return this
//    }
//
//    private fun <RESPONSE : EthResponse> addRequest(createReq: (Int) -> HotObject, resultClass: KClass<RESPONSE>) {
//        val reqId = requests.size + 1
//        resultClasses.add(resultClass)
//        requests.add(createReq(reqId))
//    }
//}