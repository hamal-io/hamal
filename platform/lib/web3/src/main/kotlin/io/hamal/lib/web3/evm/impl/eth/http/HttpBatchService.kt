package io.hamal.lib.web3.evm.impl.eth.http

import io.hamal.lib.common.hot.HotArray
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.hot.HotObjectBuilder
import io.hamal.lib.common.hot.HotObjectModule
import io.hamal.lib.common.serialization.JsonFactoryBuilder
import io.hamal.lib.domain.Json
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.web3.evm.EvmBatchService
import io.hamal.lib.web3.evm.EvmHotModule
import io.hamal.lib.web3.evm.HttpBaseBatchService
import io.hamal.lib.web3.evm.abi.type.EvmUint64
import io.hamal.lib.web3.evm.domain.EvmRequestId
import io.hamal.lib.web3.evm.impl.eth.DepEthBatchService
import io.hamal.lib.web3.evm.impl.eth.domain.*
import io.hamal.lib.web3.json
import kotlin.reflect.KClass

interface EthBatchService<SERVICE : EvmBatchService<EthResponse, SERVICE>> : EvmBatchService<EthResponse, SERVICE>

class EthHttpBatchService(
    httpTemplate: HttpTemplate,
) : EthBatchService<EthHttpBatchService>, HttpBaseBatchService<EthResponse>(httpTemplate, json) {

    override fun getBlock(number: EvmUint64) = also {
        request(
            method = "eth_getBlockByNumber",
            params = HotArray.builder()
                .append(number.toPrefixedHexString().value)
                .append(true)
                .build(),
            resultClass = EthGetBlockResponse::class
        )
    }
}

class DepEthHttpBatchService(
    private val httpTemplate: HttpTemplate
) : DepEthBatchService<DepEthHttpBatchService> {

    private val resultClasses = mutableListOf<KClass<*>>()
    private val requests = mutableListOf<HotObject>()

    override fun getBlockNumber() = request(
        method = "eth_blockNumber",
        params = HotArray.empty,
        resultClass = EthGetBlockNumberResponse::class
    )

    override fun getBlock(number: EvmUint64) = request(
        method = "eth_getBlockByNumber",
        params = HotArray.builder()
            .append(number.toPrefixedHexString().value)
            .append(true)
            .build(),
        resultClass = EthGetBlockResponse::class
    )

    override fun getLiteBlock(number: EvmUint64) = request(
        method = "eth_getBlockByNumber",
        params = HotArray.builder()
            .append(number.toPrefixedHexString().value)
            .append(false)
            .build(),
        resultClass = EthGetLiteBlockResponse::class
    )

    override fun call(callRequest: DepEthBatchService.EthCallRequest) = request(
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

    override fun lastRequestId(): EvmRequestId {
        return EvmRequestId(requests.size.toString())
    }

    override fun execute(): List<EthResponse> {
        if (requests.isEmpty()) {
            return listOf()
        }

        val response = httpTemplate
            .post()
            .body(HotArray.builder().also { builder -> requests.forEach { request -> builder.append(request) } }.build())
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
            .register(EvmHotModule)
            .register(HotObjectModule)
    )

    private fun <RESPONSE : EthResponse> request(
        method: String,
        params: HotArray,
        resultClass: KClass<RESPONSE>
    ): DepEthHttpBatchService {
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