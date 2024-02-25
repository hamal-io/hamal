package io.hamal.lib.web3.eth.http

import io.hamal.lib.common.hot.*
import io.hamal.lib.common.serialization.JsonFactoryBuilder
import io.hamal.lib.domain.Json
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.http.body
import io.hamal.lib.web3.Web3JsonModule
import io.hamal.lib.web3.eth.EthBatchService
import io.hamal.lib.web3.eth.abi.type.EthUint64
import io.hamal.lib.web3.eth.domain.EthGetBlockResponse
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
    private val requests = mutableListOf<HotObject>()

    override fun getBlockNumber() = TODO()
//        request(
//        method = "eth_blockNumber",
//        params = JsonArray(listOf()),
//        resultClass = EthGetBlockNumberResponse::class
//    )

    override fun getBlock(number: EthUint64) = request(
        method = "eth_getBlockByNumber",
        params = HotArray(
            listOf(
                HotString(number.toPrefixedHexString().value),
                HotBoolean(true)
            )
        ),
        resultClass = EthGetBlockResponse::class
    )

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
            if (requests.isEmpty()) {
                return listOf()
            }

            val response = httpTemplate
                .post("/")
                .body(HotArray.builder().also { builder -> requests.forEach { request -> builder.add(request) } }
                    .build())
                .execute(HotArray::class)


            return response.nodes.mapIndexed { index, hotNode ->
                val response = hotNode.asObject()
                json.deserialize(resultClasses[index], json.serialize(response))
            }
                .filterIsInstance<EthResponse>()
                .also {
                    requests.clear()
                    resultClasses.clear()
                }
        }
    }

    internal val json = Json(
        JsonFactoryBuilder()
            .register(HotJsonModule)
            .register(Web3JsonModule)
    )

    private fun <RESPONSE : EthResponse> request(
        method: String,
        params: HotArray,
        resultClass: KClass<RESPONSE>
    ): EthHttpBatchService {
        addRequest(
            createReq = { id ->
                HotObjectBuilder()
                    .set("jsonrpc", "2.0")
                    .set("id", id)
                    .set("method", method)
                    .set("params", params)
                    .build(
                    )
            },
            resultClass = resultClass
        )
        return this
    }

    private fun <RESPONSE : EthResponse> addRequest(createReq: (Int) -> HotObject, resultClass: KClass<RESPONSE>) {
        lock.withLock {
            val reqId = requests.size + 1
            resultClasses.add(resultClass)
            requests.add(createReq(reqId))
        }
    }
}