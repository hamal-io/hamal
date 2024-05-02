package io.hamal.lib.web3.evm.domain

import io.hamal.lib.common.serialization.json.JsonObject
import io.hamal.lib.common.serialization.json.JsonString

interface EvmResponse {
    val jsonrpc: kotlin.String
}

sealed class EvmHotResponse : EvmResponse {
    override val jsonrpc: kotlin.String = "2.0"
}

data class EvmHotGetBlockResponse(
    val id: EvmRequestId,
    val result: JsonObject?
) : EvmHotResponse() {
    override fun toString(): kotlin.String {
        return "GenericGetBlockResponse($result)"
    }
}


data class EvmHotCallResponse(
    val id: EvmRequestId,
    val result: JsonString?
) : EvmHotResponse() {
    override fun toString(): kotlin.String {
        return "EvmHotCallResponse($result)"
    }
}

data class EvmHotSendRawTransactionResponse(
    val id: EvmRequestId,
    val result: JsonString?
) : EvmHotResponse() {
    override fun toString(): kotlin.String {
        return "EvmHotSendRawTransactionResponse($result)"
    }
}
