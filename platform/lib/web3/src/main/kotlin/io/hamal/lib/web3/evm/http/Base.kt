package io.hamal.lib.web3.evm.http

import io.hamal.lib.common.serialization.SerdeJson
import io.hamal.lib.common.serialization.json.JsonArray
import io.hamal.lib.common.serialization.json.JsonNode
import io.hamal.lib.common.serialization.json.JsonObject
import io.hamal.lib.common.serialization.json.JsonObjectBuilder
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.web3.evm.domain.EvmResponse
import kotlin.reflect.KClass

abstract class HttpBaseBatchService<out RESPONSE : EvmResponse>(
    private val httpTemplate: HttpTemplate,
    private val serde: SerdeJson
) {

    fun execute(): List<RESPONSE> {
        if (requests.isEmpty()) {
            return listOf()
        }

        val response = httpTemplate
            .post()
            .body(JsonArray.builder().also { builder -> requests.forEach { request -> builder.append(request) } }.build())
            .execute(JsonNode::class)

        if (response is JsonArray) {
            return response.nodes.mapIndexed { index, hotNode ->
                val result = hotNode.asObject()
                // FIXME handle [{"id":"1","error":{"code":-32603,"message":"Unexpected error"},"jsonrpc":"2.0"}]
                serde.read(resultClasses[index], serde.write(result))
            }
                .also {
                    requests.clear()
                    resultClasses.clear()
                }
        }

        if (response is JsonObject) {
            val error = response.find("error")
            if (error is JsonObject) {
                val code = error["code"].intValue
                val message = error["message"].stringValue
                throw RuntimeException("$code - $message")
            }
        }

        TODO()
    }

    protected fun request(
        method: String,
        params: JsonArray,
        resultClass: KClass<out @UnsafeVariance RESPONSE>
    ) {
        addRequest(
            createReq = { id ->
                JsonObjectBuilder()
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

    private fun addRequest(createReq: (Int) -> JsonObject, resultClass: KClass<out RESPONSE>) {
        val reqId = requests.size + 1
        resultClasses.add(resultClass)
        requests.add(createReq(reqId))
    }

    private val resultClasses = mutableListOf<KClass<out RESPONSE>>()
    private val requests = mutableListOf<JsonObject>()
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