package io.hamal.lib.web3.evm.http

import io.hamal.lib.common.serialization.json.JsonArray
import io.hamal.lib.common.serialization.json.JsonNull
import io.hamal.lib.common.serialization.json.JsonObject
import io.hamal.lib.common.serialization.json.JsonString
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.web3.evm.EvmBatchService
import io.hamal.lib.web3.evm.abi.type.EvmAddress
import io.hamal.lib.web3.evm.abi.type.EvmPrefixedHexString
import io.hamal.lib.web3.evm.abi.type.EvmUint64
import io.hamal.lib.web3.evm.domain.EvmHotCallResponse
import io.hamal.lib.web3.evm.domain.EvmHotGetBlockResponse
import io.hamal.lib.web3.evm.domain.EvmHotResponse
import io.hamal.lib.web3.evm.domain.EvmHotSendRawTransactionResponse
import io.hamal.lib.web3.serde

interface EvmHotBatchService<SERVICE : EvmBatchService<EvmHotResponse, SERVICE>> :
    EvmBatchService<EvmHotResponse, SERVICE>

class EvmHotHttpBatchService(
    httpTemplate: HttpTemplate,
) : EvmHotBatchService<EvmHotHttpBatchService>, HttpBaseBatchService<EvmHotResponse>(httpTemplate, serde) {

    override fun getBlock(number: EvmUint64) = also {
        request(
            method = "eth_getBlockByNumber",
            params = JsonArray.builder()
                .append(number.toPrefixedHexString().value)
                .append(true)
                .build(),
            resultClass = EvmHotGetBlockResponse::class
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
            resultClass = EvmHotCallResponse::class
        )
    }

    override fun sendRawTransaction(data: EvmPrefixedHexString) = also {
        request(
            method = "eth_sendRawTransaction",
            params = JsonArray.builder().append(data.toString()).build(),
            resultClass = EvmHotSendRawTransactionResponse::class
        )
    }
}
