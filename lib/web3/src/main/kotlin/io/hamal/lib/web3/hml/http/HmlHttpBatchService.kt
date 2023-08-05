package io.hamal.lib.web3.hml.http

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.web3.eth.abi.type.EthUint64
import io.hamal.lib.web3.eth.domain.EthRequestId
import io.hamal.lib.web3.hml.HmlBatchService
import io.hamal.lib.web3.hml.domain.Chain
import io.hamal.lib.web3.hml.domain.HmlCallResponse
import io.hamal.lib.web3.hml.domain.HmlGetBlockResponse
import io.hamal.lib.web3.hml.domain.HmlResponse
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.serializer
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.reflect.KClass

class HmlHttpBatchService(
    private val httpTemplate: HttpTemplate,
    override val chain: Chain = Chain.Eth
) : HmlBatchService<HmlHttpBatchService> {

    private val lock = ReentrantLock()
    private val resultClasses = mutableListOf<KClass<*>>()
    private val requests = mutableListOf<JsonObject>()

    override fun getBlock(number: EthUint64) = request(
        method = "${chain.name.lowercase()}_get_block_by_number",
        params = JsonArray(
            listOf(
                JsonPrimitive(number.toPrefixedHexString().value),
                JsonPrimitive(true)
            )
        ),
        resultClass = HmlGetBlockResponse::class
    )

    override fun call(callRequest: HmlBatchService.CallRequest) = request(
        method = "${chain.name.lowercase()}_call",
        params = JsonArray(
            listOf(
                JsonObject(
                    mapOf(
                        "to" to JsonPrimitive(callRequest.to.toPrefixedHexString().value),
                        "data" to JsonPrimitive(callRequest.data.value)
                    )
                ),
                JsonPrimitive(callRequest.blockNumber.toPrefixedHexString().value),
            )
        ),
        resultClass = HmlCallResponse::class
    )

    override fun lastRequestId(): EthRequestId {
        return EthRequestId(requests.size)
    }

    @OptIn(InternalSerializationApi::class)
    override fun execute(): List<HmlResponse> {
        return lock.withLock {

            if (requests.isEmpty()) {
                return listOf()
            }

            val response = httpTemplate
                .post("/hml")
                .body(JsonArray(requests))
                .execute(JsonArray::class)

            response.zip(resultClasses)
                .map { (response, resultClass) -> json.decodeFromJsonElement(resultClass.serializer(), response) }
                .filterIsInstance<HmlResponse>()
                .also {
                    requests.clear()
                    resultClasses.clear()
                }
        }
    }

    private fun <RESPONSE : HmlResponse> request(
        method: String,
        params: JsonArray,
        resultClass: KClass<RESPONSE>
    ): HmlHttpBatchService {
        addRequest(
            createReq = { id ->
                JsonObject(
                    mapOf(
                        "jsonrpc" to JsonPrimitive("2.0"),
                        "id" to JsonPrimitive(id),
                        "method" to JsonPrimitive(method),
                        "params" to params,
                    )
                )
            },
            resultClass = resultClass
        )
        return this
    }

    private fun <RESPONSE : HmlResponse> addRequest(createReq: (Int) -> JsonObject, resultClass: KClass<RESPONSE>) {
        lock.withLock {
            val reqId = requests.size + 1
            resultClasses.add(resultClass)
            requests.add(createReq(reqId))
        }
    }

    private val json = Json { ignoreUnknownKeys = true }
}