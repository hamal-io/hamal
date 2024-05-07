package io.hamal.lib.web3.evm.chain.arbitrum.http

import io.hamal.lib.common.serialization.json.JsonArray
import io.hamal.lib.common.serialization.json.JsonNull
import io.hamal.lib.common.serialization.json.JsonObject
import io.hamal.lib.common.serialization.json.JsonString
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.web3.evm.EvmBatchService
import io.hamal.lib.web3.evm.abi.type.EvmAddress
import io.hamal.lib.web3.evm.abi.type.EvmPrefixedHexString
import io.hamal.lib.web3.evm.abi.type.EvmUint64
import io.hamal.lib.web3.evm.chain.arbitrum.domain.ArbitrumCallResponse
import io.hamal.lib.web3.evm.chain.arbitrum.domain.ArbitrumGetBlockResponse
import io.hamal.lib.web3.evm.chain.arbitrum.domain.ArbitrumResponse
import io.hamal.lib.web3.evm.http.HttpBaseBatchService
import io.hamal.lib.web3.serde

interface ArbitrumBatchService<SERVICE : EvmBatchService<ArbitrumResponse, SERVICE>> :
    EvmBatchService<ArbitrumResponse, SERVICE>

class ArbitrumHttpBatchService(
    httpTemplate: HttpTemplate,
) : ArbitrumBatchService<ArbitrumHttpBatchService>, HttpBaseBatchService<ArbitrumResponse>(httpTemplate, serde) {

    override fun getBlock(number: EvmUint64) = also {
        request(
            method = "eth_getBlockByNumber",
            params = JsonArray.builder()
                .append(number.toPrefixedHexString().value)
                .append(true)
                .build(),
            resultClass = ArbitrumGetBlockResponse::class
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
            resultClass = ArbitrumCallResponse::class
        )
    }

    override fun sendRawTransaction(data: EvmPrefixedHexString): ArbitrumHttpBatchService {
        TODO("Not yet implemented")
    }
}
