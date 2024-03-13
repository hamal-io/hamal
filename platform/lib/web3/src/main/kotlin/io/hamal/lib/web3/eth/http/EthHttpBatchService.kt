package io.hamal.lib.web3.eth.http

import io.hamal.lib.common.hot.HotArray
import io.hamal.lib.common.hot.HotJsonModule
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.hot.HotObjectBuilder
import io.hamal.lib.common.serialization.JsonFactoryBuilder
import io.hamal.lib.domain.Json
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.web3.Web3JsonModule
import io.hamal.lib.web3.eth.EthBatchService
import io.hamal.lib.web3.eth.abi.type.EthUint64
import io.hamal.lib.web3.eth.domain.*
import kotlin.reflect.KClass

class EthHttpBatchService(
    private val httpTemplate: HttpTemplate
) : EthBatchService<EthHttpBatchService> {

    private val resultClasses = mutableListOf<KClass<*>>()
    private val requests = mutableListOf<HotObject>()

    override fun getBlockNumber() = request(
        method = "eth_blockNumber",
        params = HotArray.empty,
        resultClass = EthGetBlockNumberResponse::class
    )

    override fun getBlock(number: EthUint64) = request(
        method = "eth_getBlockByNumber",
        params = HotArray.builder()
            .append(number.toPrefixedHexString().value)
            .append(true)
            .build(),
        resultClass = EthGetBlockResponse::class
    )

    override fun getLiteBlock(number: EthUint64) = request(
        method = "eth_getBlockByNumber",
        params = HotArray.builder()
            .append(number.toPrefixedHexString().value)
            .append(false)
            .build(),
        resultClass = EthGetLiteBlockResponse::class
    )

    override fun call(callRequest: EthBatchService.EthCallRequest) = request(
        method = "eth_call",
        params = HotArray.builder()
            .append(
                HotObject.builder()
                    .set("to", callRequest.to.toPrefixedHexString().value)
                    .set("data", callRequest.data.value)
                    .build()
            )
            .append(callRequest.blockNumber.toPrefixedHexString().value)
            .build(),
        resultClass = EthCallResponse::class
    )

    override fun lastRequestId(): EthRequestId {
        return EthRequestId(requests.size.toString())
    }

    override fun execute(): List<EthResponse> {
        if (requests.isEmpty()) {
            return listOf()
        }

        val response = httpTemplate
            .post("/")
            .body(HotArray.builder().also { builder -> requests.forEach { request -> builder.append(request) } }
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
                    .set("id", id.toString())
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
        val reqId = requests.size + 1
        resultClasses.add(resultClass)
        requests.add(createReq(reqId))
    }
}