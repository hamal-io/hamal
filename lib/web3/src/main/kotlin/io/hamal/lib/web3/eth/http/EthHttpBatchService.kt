package io.hamal.lib.web3.eth.http

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.web3.eth.EthBatchService
import io.hamal.lib.web3.eth.abi.type.EthHash
import io.hamal.lib.web3.eth.abi.type.EthUint64
import io.hamal.lib.web3.eth.domain.EthGetBlockNumberResp
import io.hamal.lib.web3.eth.domain.EthGetBlockResp
import io.hamal.lib.web3.eth.domain.EthGetLiteBlockResp
import io.hamal.lib.web3.eth.domain.EthResp
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.serializer
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.reflect.KClass

class EthHttpBatchService : EthBatchService<EthHttpBatchService> {

    private val lock = ReentrantLock()
    private val resultClasses = mutableListOf<KClass<*>>()
    private val requests = mutableListOf<JsonObject>()

    override fun getBlockNumber() = request(
        method = "eth_blockNumber",
        params = JsonArray(listOf()),
        resultClass = EthGetBlockNumberResp::class
    )

    override fun getBlock(hash: EthHash) = request(
        method = "eth_getBlockByHash",
        params = JsonArray(
            listOf(
                JsonPrimitive(hash.toPrefixedHexString().value),
                JsonPrimitive(true)
            )
        ),
        resultClass = EthGetBlockResp::class
    )

    override fun getBlock(number: EthUint64) = request(
        method = "eth_getBlockByNumber",
        params = JsonArray(
            listOf(
                JsonPrimitive(number.toPrefixedHexString().value),
                JsonPrimitive(true)
            )
        ),
        resultClass = EthGetBlockResp::class
    )

    override fun getLiteBlock(hash: EthHash) = request(
        method = "eth_getBlockByHash",
        params = JsonArray(
            listOf(
                JsonPrimitive(hash.toPrefixedHexString().value),
                JsonPrimitive(false)
            )
        ),
        resultClass = EthGetLiteBlockResp::class
    )

    override fun getLiteBlock(number: EthUint64) = request(
        method = "eth_getBlockByNumber",
        params = JsonArray(
            listOf(
                JsonPrimitive(number.toPrefixedHexString().value),
                JsonPrimitive(false)
            )
        ),
        resultClass = EthGetLiteBlockResp::class
    )

    @OptIn(InternalSerializationApi::class)
    override fun execute(): List<EthResp> {
        return lock.withLock {
            val response = HttpTemplate("https://cloudflare-eth.com")
                .post("/")
                .body(JsonArray(requests))
                .execute(JsonArray::class)

            response.zip(resultClasses)
                .map { (response, resultClass) -> json.decodeFromJsonElement(resultClass.serializer(), response) }
                .filterIsInstance<EthResp>()
                .also {
                    requests.clear()
                    resultClasses.clear()
                }
        }
    }

    private fun <RESPONSE : EthResp> request(
        method: String,
        params: JsonArray,
        resultClass: KClass<RESPONSE>
    ): EthHttpBatchService {
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

    private fun <RESPONSE : EthResp> addRequest(createReq: (Int) -> JsonObject, resultClass: KClass<RESPONSE>) {
        lock.withLock {
            val reqId = requests.size + 1
            resultClasses.add(resultClass)
            requests.add(createReq(reqId))
        }
    }

    private val json = Json { ignoreUnknownKeys = true }
}