package io.hamal.lib.web3.eth.http

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.web3.eth.EthBatchService
import io.hamal.lib.web3.eth.abi.type.EthUint64
import io.hamal.lib.web3.eth.domain.EthRequestId
import io.hamal.lib.web3.eth.domain.EthResponse
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.reflect.KClass

class EthHttpBatchService(
    private val httpTemplate: HttpTemplateImpl
) : EthBatchService<EthHttpBatchService> {

    private val lock = ReentrantLock()
    private val resultClasses = mutableListOf<KClass<*>>()
    private val requests = mutableListOf<JsonObject>()

    override fun getBlockNumber() = TODO()
//        request(
//        method = "eth_blockNumber",
//        params = JsonArray(listOf()),
//        resultClass = EthGetBlockNumberResponse::class
//    )

    override fun getBlock(number: EthUint64) = TODO()
//        request(
//        method = "eth_getBlockByNumber",
//        params = JsonArray(
//            listOf(
//                JsonPrimitive(number.toPrefixedHexString().value),
//                JsonPrimitive(true)
//            )
//        ),
//        resultClass = EthGetBlockResponse::class
//    )

    override fun getLiteBlock(number: EthUint64) = TODO()
//        request(
//        method = "eth_getBlockByNumber",
//        params = JsonArray(
//            listOf(
//                JsonPrimitive(number.toPrefixedHexString().value),
//                JsonPrimitive(false)
//            )
//        ),
//        resultClass = EthGetLiteBlockResponse::class
//    )

    override fun call(callRequest: EthBatchService.EthCallRequest) = TODO()
//        request(
//        method = "eth_call",
//        params = JsonArray(
//            listOf(
//                JsonObject(
//                    mapOf(
//                        "to" to JsonPrimitive(callRequest.to.toPrefixedHexString().value),
//                        "data" to JsonPrimitive(callRequest.data.value)
//                    )
//                ),
//                JsonPrimitive(callRequest.blockNumber.toPrefixedHexString().value),
//            )
//        ),
//        resultClass = EthCallResponse::class
//    )

    override fun lastRequestId(): EthRequestId {
        return EthRequestId(requests.size)
    }

    override fun execute(): List<EthResponse> {
        return lock.withLock {
            TODO()
//            if (requests.isEmpty()) {
//                return listOf()
//            }
//
//            val response = httpTemplate
//                .post("/")
//                .body(JsonArray(requests))
//                .execute(JsonArray::class)
//
//            response.zip(resultClasses)
//                .map { (response, resultClass) -> json.decodeFromJsonElement(resultClass.serializer(), response) }
//                .filterIsInstance<EthResponse>()
//                .also {
//                    requests.clear()
//                    resultClasses.clear()
//                }
        }
    }

    private fun <RESPONSE : EthResponse> request(
        method: String,
        params: JsonArray,
        resultClass: KClass<RESPONSE>
    ): EthHttpBatchService {
//        addRequest(
//            createReq = { id ->
//                JsonObject(
//                    mapOf(
//                        "jsonrpc" to JsonPrimitive("2.0"),
//                        "id" to JsonPrimitive(id),
//                        "method" to JsonPrimitive(method),
//                        "params" to params,
//                    )
//                )
//            },
//            resultClass = resultClass
//        )
        TODO()
        return this
    }

    private fun <RESPONSE : EthResponse> addRequest(createReq: (Int) -> JsonObject, resultClass: KClass<RESPONSE>) {
        lock.withLock {
            val reqId = requests.size + 1
            resultClasses.add(resultClass)
            requests.add(createReq(reqId))
        }
    }
}