package io.hamal.lib.web3.evm.chain.eth.http

import io.hamal.lib.common.serialization.json.JsonArray
import io.hamal.lib.common.serialization.json.JsonNull
import io.hamal.lib.common.serialization.json.JsonObject
import io.hamal.lib.common.serialization.json.JsonString
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.web3.evm.EvmBatchService
import io.hamal.lib.web3.evm.abi.type.EvmAddress
import io.hamal.lib.web3.evm.abi.type.EvmPrefixedHexString
import io.hamal.lib.web3.evm.abi.type.EvmUint64
import io.hamal.lib.web3.evm.chain.eth.domain.EthCallResponse
import io.hamal.lib.web3.evm.chain.eth.domain.EthGetBlockResponse
import io.hamal.lib.web3.evm.chain.eth.domain.EthResponse
import io.hamal.lib.web3.evm.http.HttpBaseBatchService
import io.hamal.lib.web3.json

interface EthBatchService<SERVICE : EvmBatchService<EthResponse, SERVICE>> : EvmBatchService<EthResponse, SERVICE>

class EthHttpBatchService(
    httpTemplate: HttpTemplate,
) : EthBatchService<EthHttpBatchService>, HttpBaseBatchService<EthResponse>(httpTemplate, json) {

    override fun getBlock(number: EvmUint64) = also {
        request(
            method = "eth_getBlockByNumber",
            params = JsonArray.builder()
                .append(number.toPrefixedHexString().value)
                .append(true)
                .build(),
            resultClass = EthGetBlockResponse::class
        )
    }

    override fun call(to: EvmAddress, data: EvmPrefixedHexString, number: EvmUint64, from: EvmAddress?) = also {
        request(
            method = "eth_call",
            params = JsonArray.builder()
                .append(
                    JsonObject.builder()
                        .set("from", from?.toPrefixedHexString()?.value?.let(::JsonString) ?: JsonNull)
                        .set("to", to.toPrefixedHexString().value)
                        .set("data", data.value)
                        .build()
                )
                .append(JsonString(number.toPrefixedHexString().value))
                .build(),
            resultClass = EthCallResponse::class
        )
    }

    override fun sendRawTransaction(data: EvmPrefixedHexString): EthHttpBatchService {
        TODO("Not yet implemented")
    }
}
